package amymialee.icyincitement.common.items;

import amymialee.icyincitement.IcyIncitement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

            int balls = 1;
            if (itemStack.getItem() == Items.SNOW_BLOCK) {
                balls = 4;
            } else if (itemStack.getItem() == Items.SNOW) {
                balls = 2;
            }

            if (!world.isClient) {
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

            vec.scale(-0.0875f * (1 + (0.3f * (balls - 1))));
            user.addVelocity(vec.getX(), vec.getY(), vec.getZ());

            if (pitch >= 30) {
                user.fallDistance -= balls;
                if (user.fallDistance < 0) {
                    user.fallDistance = 0;
                }
            }

            if (!user.getAbilities().creativeMode) itemStack.decrement(1);

            user.getItemCooldownManager().set(asItem(), 2);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SnowGolemEntity) {
            ItemEntity itemEntity = new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(IcyIncitement.SNOWBALL_SPRINKLER));
            itemEntity.setPickupDelay(0);
            itemEntity.setVelocity(itemEntity.getVelocity().add(
                    (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1f,
                    entity.getRandom().nextFloat() * 0.05f,
                    (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1f)
            );
            entity.world.spawnEntity(itemEntity);
            stack.decrement(1);
            for (int i = 0; i < 20; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                entity.world.addParticle(ParticleTypes.POOF, entity.getParticleX(1.0), entity.getRandomBodyY(), entity.getParticleZ(1.0), d, e, f);
            }
            entity.world.playSoundFromEntity(null, itemEntity, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 0.6f, 1);
            entity.discard();
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.setDamage(4);
        super.onCraft(stack, world, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText(stack.getTranslationKey()+".description_1").formatted(Formatting.AQUA));
        tooltip.add(new TranslatableText(stack.getTranslationKey()+".description_2").formatted(Formatting.DARK_AQUA));
    }
}
