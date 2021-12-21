package amymialee.icyincitement.common.items;

import amymialee.icyincitement.IcyIncitement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class EmptySprinklerItem extends RangedWeaponItem {
    public static final Predicate<ItemStack> SNOWBALLS = stack -> stack.isIn(IcyIncitement.SNOWBALLS);

    public EmptySprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return SNOWBALLS;
    }

    @Override
    public int getRange() {
        return 48;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemStack itemStack = user.getArrowType(stack);
        if (itemStack.getCount() > 0) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.1f, 1f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

            if (!world.isClient) {
                int balls = 1;
                if (itemStack.getItem() == Items.SNOW_BLOCK) {
                    balls = 4;
                } else if (itemStack.getItem() == Items.SNOW) {
                    balls = 2;
                }
                for (int i = 0; i < balls; i++) {
                    SnowballEntity snowballEntity = new SnowballEntity(world, user);
                    snowballEntity.setItem(Items.SNOWBALL.getDefaultStack());
                    snowballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 3.5f, balls > 1 ? 2f : 0f);
                    world.spawnEntity(snowballEntity);
                }
            }

            float yaw = user.getHeadYaw();
            float pitch = user.getPitch();

            float f = -MathHelper.sin(yaw * ((float) Math.PI / 180)) * MathHelper.cos(pitch * ((float) Math.PI / 180)) * .25f;
            float g = -MathHelper.sin(pitch * ((float) Math.PI / 180));
            float h = MathHelper.cos(yaw * ((float) Math.PI / 180)) * MathHelper.cos(pitch * ((float) Math.PI / 180)) * .25f;
            Vec3f vec = new Vec3f(f, g, h);

            vec.scale(-0.175f);
            user.addVelocity(vec.getX(), vec.getY(), vec.getZ());

            if (pitch >= 30) {
                user.fallDistance -= 1;
                if (user.fallDistance < 0) {
                    user.fallDistance = 0;
                }
            }

            itemStack.decrement(1);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SnowGolemEntity) {
            ItemEntity itemEntity = user.dropItem(IcyIncitement.SNOWBALL_SPRINKLER, 1);
            if (itemEntity != null) {
                itemEntity.setVelocity(itemEntity.getVelocity().add(
                        (user.getRandom().nextFloat() - user.getRandom().nextFloat()) * 0.1f,
                        user.getRandom().nextFloat() * 0.05f,
                        (user.getRandom().nextFloat() - user.getRandom().nextFloat()) * 0.1f)
                );
            }
            stack.decrement(1);
            for (int i = 0; i < 20; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                entity.world.addParticle(ParticleTypes.POOF, entity.getParticleX(1.0), entity.getRandomBodyY(), entity.getParticleZ(1.0), d, e, f);
            }
            entity.discard();
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.setDamage(4);
        super.onCraft(stack, world, player);
    }
}
