package amymialee.icyincitement.items;

import amymialee.icyincitement.entity.RapidSnowballEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SnowballSprinklerItem extends Item {
    public SnowballSprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (stack.getDamage() < 192) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.1f, 1f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            if (!world.isClient) {
                RapidSnowballEntity snowballEntity = new RapidSnowballEntity(world, user);
                snowballEntity.setItem(Items.SNOWBALL.getDefaultStack());
                snowballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 2.5f, 3.5f);
                world.spawnEntity(snowballEntity);
            }

            float yaw = user.getHeadYaw();
            float pitch = user.getPitch();

            float f = -MathHelper.sin(yaw * ((float) Math.PI / 180)) * MathHelper.cos(pitch * ((float) Math.PI / 180)) * .25f;
            float g = -MathHelper.sin(pitch * ((float) Math.PI / 180));
            float h = MathHelper.cos(yaw * ((float) Math.PI / 180)) * MathHelper.cos(pitch * ((float) Math.PI / 180)) * .25f;
            Vec3f vec = new Vec3f(f, g, h);

            vec.scale(-0.075f);
            user.addVelocity(vec.getX(), vec.getY(), vec.getZ());

            if (pitch >= 30) {
                user.fallDistance -= 1;
                if (user.fallDistance < 0) {
                    user.fallDistance = 0;
                }
            }

            stack.setDamage(stack.getDamage() + 4);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        stack.setDamage(stack.getDamage() - 1);
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(stack.getTranslationKey()+".description_1").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable(stack.getTranslationKey()+".description_2").formatted(Formatting.DARK_AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    }
}