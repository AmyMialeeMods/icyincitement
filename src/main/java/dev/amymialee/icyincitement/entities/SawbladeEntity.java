package dev.amymialee.icyincitement.entities;

import dev.amymialee.icyincitement.IcyIncitement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class SawbladeEntity extends Projectile {
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(SawbladeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DEFLECTED = SynchedEntityData.defineId(SawbladeEntity.class, EntityDataSerializers.BOOLEAN);
    public Quaternionf prevRotation = new Quaternionf().set(0, 0, 0, 1);
    public Quaternionf rotation = new Quaternionf().set(0, 0, 0, 1);

    public SawbladeEntity(EntityType<? extends SawbladeEntity> entityType, Level world) {
        super(entityType, world);
        if (world.isClientSide()) {
            this.prevRotation = new Quaternionf().set(0, 0, 0, 1);
            this.rotation = new Quaternionf().set(0, 0, 0, 1);
        }
    }

    public SawbladeEntity(Level world, Player owner) {
        this(IcyIncitement.SAWBLADE, world);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entityData.get(RETURNING)) {
            if (!(this.getOwner() instanceof Player player)) {
                if (!this.level().isClientSide()) this.discard();
                return;
            }
            var difference = player.mialib$getBodyPos(0.5).subtract(this.position());
            if (difference.multiply(1, 0.35, 1).length() < 0.5) {
                if (!this.level().isClientSide()) {
                    this.level().playSound(null, player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.2F, 0.85f + 0.3f * this.random.nextFloat());
                    this.discard();
                }
                return;
            }
            if (this.getOwner().getRootVehicle() instanceof AbstractHorse) difference = difference.scale(2);
            if (difference.length() < 2) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.6).add(difference.scale(0.5)));
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8).add(difference.scale(0.05)));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.hurtMarked = true;
            this.updateRotation();
            var pos = this.position();
            var velocity = this.getDeltaMovement();
            var next = pos.add(velocity);
            for (var entity : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(2.0), entity -> entity instanceof ItemEntity || entity instanceof ExperienceOrb)) {
                if (entity.getBoundingBox().inflate(1.0f).clip(pos.subtract(velocity), next).isPresent()) continue;
                this.hitTargetOrDeflectSelf(new EntityHitResult(entity));
            }
            return;
        }
        var oldPos = this.position();
        var oldVelocity = this.getDeltaMovement();
        this.move(MoverType.SELF, oldVelocity);
        var change = this.getDeltaMovement().subtract(oldVelocity);
        this.setDeltaMovement(this.getDeltaMovement().add(change).add(0, -this.getGravity(), 0));
        var pos = this.position();
        var velocity = this.getDeltaMovement();
        var next = pos.add(velocity);
        this.hurtMarked = true;
        if (change.length() > 0.01) {
            HitResult hit = this.level().clip(new ClipContext(oldPos, oldPos.add(oldVelocity), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit instanceof BlockHitResult blockHitResult) {
                if (!this.level().isClientSide()) {
                    this.onHitBlock(blockHitResult);
                    var blockPos = blockHitResult.getBlockPos();
                    this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, this.level().getBlockState(blockPos)));
                }
                return;
            }
        }
        if (!this.level().isClientSide()) {
            HitResult hit = this.level().clip(new ClipContext(pos, next, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) next = hit.getLocation();
            for (var entity : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity)) {
                if (entity.getType() == IcyIncitement.SAWBLADE) {
                    if (entity.getBoundingBox().inflate(1.0f).clip(pos.subtract(velocity), next).isEmpty()) continue;
                } else {
                    if (entity.getBoundingBox().inflate(0.3f).clip(pos.subtract(velocity), next).isEmpty()) continue;
                }
                this.setPos(next);
                this.hitTargetOrDeflectSelf(new EntityHitResult(entity));
            }
            for (var entity : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), entity -> entity instanceof ItemEntity || entity instanceof ExperienceOrb)) {
                if (entity.getBoundingBox().inflate(1.0f).clip(pos.subtract(velocity), next).isPresent()) continue;
                this.hitTargetOrDeflectSelf(new EntityHitResult(entity));
            }
        }
        this.updateRotation();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        this.setDeltaMovement(this.getDeltaMovement().scale(-1));
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        var blockState = this.level().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.level(), blockState, blockHitResult, this);
        if (!this.level().isClientSide()) {
            this.entityData.set(RETURNING, true);
            this.noPhysics = true;
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (entity.isSpectator()) return false;
        if (entity.getRootVehicle() == this.getRootVehicle()) return false;
        if (!entity.canBeHitByProjectile() && entity.getType() != EntityType.END_CRYSTAL && !(entity instanceof EnderDragonPart) && entity.getType() != IcyIncitement.SAWBLADE) return false;
        var owner = this.getOwner();
        if (owner == null) return true;
        if (entity == owner || owner.isPassengerOfSameVehicle(entity)) return false;
        return !(owner instanceof Player player) || !(entity instanceof Player target) || player.canHarmPlayer(target);
    }

    @Override
    public void updateRotation() {
        super.updateRotation();
        this.prevRotation.set(this.rotation);
        var direction = this.getDeltaMovement().multiply(1, 0, 1);
        if (direction.length() > 0.01) {
            var targetDirection = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z);
            var rotationQuat = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), targetDirection);
            var goal = new Quaternionf().set(rotationQuat).mul(this.rotation);
            this.rotation.slerp(goal, (float) direction.length() / 2);
        }
        while (this.getYRot() - this.yRotO < -180.0F) this.yRotO -= 360.0F;
        while (this.getYRot() - this.yRotO >= 180.0F) this.yRotO += 360.0F;
        while (this.getXRot() - this.xRotO < -180.0F) this.xRotO -= 360.0F;
        while (this.getXRot() - this.xRotO >= 180.0F) this.xRotO += 360.0F;
        if (this.level().isClientSide()) this.playWindSound();
    }

    @Environment(EnvType.CLIENT)
    private void playWindSound() {
        var prevPos = new Vec3(this.xo, this.yo, this.zo);
        var pos = new Vec3(this.getX(), this.getY(), this.getZ());
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var prevPlayerPos = new Vec3(player.xo, player.yo, player.zo);
        var playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
        var prevDistance = prevPos.distanceTo(prevPlayerPos);
        var distance = pos.distanceTo(playerPos);
        var change = distance - prevDistance;
        var pitch = (float) Mth.clamp(change / 6, -0.75, 0.75f);
        this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.35f, 1.25f - pitch);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.entityData.set(RETURNING, valueInput.getBooleanOr("Returning", false));
        this.entityData.set(DEFLECTED, valueInput.getBooleanOr("Deflected", false));
        super.readAdditionalSaveData(valueInput);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        valueOutput.putBoolean("Returning", this.entityData.get(RETURNING));
        valueOutput.putBoolean("Deflected", this.entityData.get(DEFLECTED));
        super.addAdditionalSaveData(valueOutput);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(RETURNING, false);
        builder.define(DEFLECTED, false);
    }
}