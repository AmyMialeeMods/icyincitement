package xyz.amymialee.icyincitement.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.icyincitement.IcyIncitement;

public class IceBallEntity extends ProjectileEntity {
	public IceBallEntity(EntityType<? extends IceBallEntity> entityType, World world) {
		super(entityType, world);
	}

	public IceBallEntity(World world, LivingEntity owner) {
		super(IcyIncitement.ICEBALL, world);
		this.setOwner(owner);
		this.setPosition(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.firstUpdate) for (var blockPos : BlockPos.iterate(this.getBoundingBox())) {
			var blockState = this.getWorld().getBlockState(blockPos);
			if (blockState.isOf(Blocks.BUBBLE_COLUMN)) blockState.onEntityCollision(this.getWorld(), blockPos, this);
		}
		this.applyGravity();
		var velocity = this.getVelocity();
		var pos = this.getPos();
		float drag;
		if (this.isTouchingWater()) {
			for (var i = 0; i < 4; i++) this.getWorld().addParticle(ParticleTypes.BUBBLE, pos.x - velocity.x * 0.25, pos.y - velocity.y * 0.25, pos.z - velocity.z * 0.25, velocity.x, velocity.y, velocity.z);
			drag = 0.8F;
		} else {
			drag = 0.99F;
		}
		this.setVelocity(velocity.multiply(drag));
        var hitResult = ProjectileUtil.getCollision(this, this::canHit);
		Vec3d nextPos;
		if (hitResult.getType() != HitResult.Type.MISS) {
			nextPos = hitResult.getPos();
		} else {
			nextPos = this.getPos().add(this.getVelocity());
		}
		this.setPosition(nextPos);
		this.updateRotation();
		this.tickBlockCollision();
		super.tick();
		if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) this.hitOrDeflect(hitResult);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
		this.discard();
	}

	@Override
	protected void onEntityHit(@NotNull EntityHitResult entityHitResult) {
		if (!(this.getWorld() instanceof ServerWorld)) return;
		var entity = entityHitResult.getEntity();
		entity.timeUntilRegen = 0;
		if (!entity.canFreeze()) {
			if (entity instanceof LivingEntity living) living.heal(0.25f);
			return;
		}
		var frozen = entity.getFrozenTicks();
		entity.setFrozenTicks(Math.min(entity.getMinFreezeDamageTicks() * 2, frozen + (frozen > entity.getMinFreezeDamageTicks() ? 4 : 16)));
		entity.serverDamage(this.getDamageSources().create(DamageTypes.FREEZE, this, this.getOwner()), 0.05f);
	}

	@Override
	public void handleStatus(byte status) {
		if (status != EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) return;
		for (var i = 0; i < 8; i++) this.getWorld().addParticle(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
	}

	@Override
	protected double getGravity() {
		return 0.03;
	}

	@Override
	public boolean shouldRender(double distance) {
		if (this.age < 4 && distance < 12.25) return false;
		var d = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(d)) d = 4.0;
		d *= 64.0;
		return distance < d * d;
	}

	@Override
	public boolean canUsePortals(boolean allowVehicles) {
		return true;
	}

	@Override
	protected void initDataTracker(DataTracker.@NotNull Builder builder) {}
}