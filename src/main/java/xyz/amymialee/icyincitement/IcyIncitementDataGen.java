package xyz.amymialee.icyincitement;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.templates.MDataGen;

import java.util.function.Consumer;

public class IcyIncitementDataGen extends MDataGen {
    protected @Override void generateTranslations(@NotNull MLanguageProvider provider, RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        builder.add(IcyIncitement.EMPTY_SPRINKLER, "Empty Sprinkler");
        builder.add(IcyIncitement.SNOWBALL_SPRINKLER, "Snowball Sprinkler");
        builder.add(IcyIncitement.CATEGORY.getTranslationKey(), "Icy Incitement");
        builder.add(IcyIncitement.MAX_SNOWBALLS.getTranslationKey(), "Max Snowballs");
        builder.add(IcyIncitement.MAX_SNOWBALLS.getDescriptionTranslationKey(), "The maximum number of snowballs the Snowball Sprinkler can hold");
        builder.add(IcyIncitement.RECHARGE_RATE.getTranslationKey(), "Recharge Rate");
        builder.add(IcyIncitement.RECHARGE_RATE.getDescriptionTranslationKey(), "The rate at which the Snowball Sprinkler recharges snowballs per tick\nAt a rate of 1, the meter will never go down");
        builder.add(IcyIncitement.RECOIL.getTranslationKey(), "Recoil");
        builder.add(IcyIncitement.RECOIL.getDescriptionTranslationKey(), "The amount of recoil the Snowball Sprinkler applies to the player when fired");
        builder.add(IcyIncitement.SLOWDOWN.getTranslationKey(), "Slowdown");
        builder.add(IcyIncitement.SLOWDOWN.getDescriptionTranslationKey(), "Should you be slowed while firing the Snowball Sprinkler");
        builder.add(IcyIncitement.SNOW_DAMAGE.getTranslationKey(), "Snow Damage");
        builder.add(IcyIncitement.SNOW_DAMAGE.getDescriptionTranslationKey(), "How much should the Snowball Sprinkler damage enemies per hit");
        builder.add(IcyIncitement.SNOW_HEALING.getTranslationKey(), "Snow Healing");
        builder.add(IcyIncitement.SNOW_HEALING.getDescriptionTranslationKey(), "How much should the Snowball Sprinkler heal snowy entities per hit");
        builder.add(IcyIncitement.SNOW_GRAVITY.getTranslationKey(), "Snow Gravity");
        builder.add(IcyIncitement.SNOW_GRAVITY.getDescriptionTranslationKey(), "How much gravity should Snowball Sprinkler snowballs experience?");
        builder.add("advancements.icyincitement.title", "Icy Incitement");
        builder.add("advancements.icyincitement.description", "Fill an Empty Sprinkler with a real snow production source");
    }

    protected @Override void generateAdvancements(MAdvancementProvider provider, RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        Advancement.Builder.create()
                .parent(Advancement.Builder.create().build((a) -> {}, "adventure/root"))
                .display(IcyIncitement.SNOWBALL_SPRINKLER,
                        Text.translatable("advancements.icyincitement.title"),
                        Text.translatable("advancements.icyincitement.description"),
                        null, AdvancementFrame.CHALLENGE, true, true, false)
                .criterion("sprinkler", InventoryChangedCriterion.Conditions.items(IcyIncitement.SNOWBALL_SPRINKLER))
                .build(consumer, IcyIncitement.id("sprinkler").toString());
    }

    protected @Override void generateRecipes(MRecipeProvider provider, RegistryWrapper.@NotNull WrapperLookup registryLookup, RecipeExporter exporter) {
        var lookup = registryLookup.getOrThrow(RegistryKeys.ITEM);
        ShapedRecipeJsonBuilder.create(lookup, RecipeCategory.COMBAT, IcyIncitement.EMPTY_SPRINKLER)
                .pattern("DI ")
                .pattern("IPQ")
                .pattern(" CS")
                .input('D', Items.DISPENSER)
                .input('I', Items.IRON_INGOT)
                .input('P', Items.PACKED_ICE)
                .input('Q', Items.QUARTZ_STAIRS)
                .input('C', Items.CHEST)
                .input('S', Items.SNOW_BLOCK)
                .criterion("has_dispenser", RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(lookup, Items.DISPENSER)))
                .criterion("has_iron_ingot", RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(lookup, Items.IRON_INGOT)))
                .criterion("has_packed_ice", RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(lookup, Items.PACKED_ICE)))
                .criterion("has_quartz_stairs", RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(lookup, Items.QUARTZ_STAIRS)))
                .criterion("has_chest", RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(lookup, Items.CHEST)))
                .criterion("has_snow_block", RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(lookup, Items.SNOW_BLOCK)))
                .offerTo(exporter);
    }
}