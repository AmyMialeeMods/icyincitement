package dev.amymialee.icyincitement;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import xyz.amymialee.mialib.templates.MDataGen;

import java.util.function.Consumer;

public class IcyIncitementDataGenerator extends MDataGen {
	protected @Override void generateTranslations(@NotNull MLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
		builder.add(IcyIncitement.EMPTY_SPRINKLER, "Empty Sprinkler");
		builder.add(IcyIncitement.SNOWBALL_SPRINKLER, "Snowball Sprinkler");

		builder.add(IcyIncitement.CATEGORY.getTranslationKey(), "Icy Incitement");
		builder.add(IcyIncitement.MAX_SNOWBALLS.getTranslationKey(), "Max Snowballs");
		builder.add(IcyIncitement.MAX_SNOWBALLS.getDescriptionTranslationKey(), "The maximum number of snowballs the Snowball Sprinkler can hold");
		builder.add(IcyIncitement.RECHARGE_RATE.getTranslationKey(), "Recharge Rate");
		builder.add(IcyIncitement.RECHARGE_RATE.getDescriptionTranslationKey(), "The rate at which the Snowball Sprinkler recharges snowballs per tick\nAt a rate of 1, the meter will recharge instantly");
		builder.add(IcyIncitement.HORIZONTAL_RECOIL.getTranslationKey(), "Horizontal Recoil");
		builder.add(IcyIncitement.HORIZONTAL_RECOIL.getDescriptionTranslationKey(), "The amount of recoil the Snowball Sprinkler applies to the player horizontally when fired");
		builder.add(IcyIncitement.VERTICAL_RECOIL.getTranslationKey(), "Vertical Recoil");
		builder.add(IcyIncitement.VERTICAL_RECOIL.getDescriptionTranslationKey(), "The amount of recoil the Snowball Sprinkler applies to the player vertically when fired");
		builder.add(IcyIncitement.SNOW_VELOCITY.getTranslationKey(), "Snow Velocity");
		builder.add(IcyIncitement.SNOW_VELOCITY.getDescriptionTranslationKey(), "How fast do Sprinkler Snowballs fly at");
		builder.add(IcyIncitement.SNOW_DIVERGENCE.getTranslationKey(), "Snow Divergence");
		builder.add(IcyIncitement.SNOW_DIVERGENCE.getDescriptionTranslationKey(), "How much spread do the shots fired by the Snowball Sprinkler have");

		builder.add("advancements.icyincitement.title", "Icy Incitement");
		builder.add("advancements.icyincitement.description", "Fill an Empty Sprinkler with a real snow production source");
	}

	protected @Override void generateAdvancements(MAdvancementProvider provider, HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
		Advancement.Builder.advancement()
				.parent(Advancement.Builder.advancement().save((a) -> {}, "adventure/root"))
				.display(IcyIncitement.SNOWBALL_SPRINKLER,
						Component.translatable("advancements.icyincitement.title"),
						Component.translatable("advancements.icyincitement.description"),
						null, AdvancementType.CHALLENGE, true, true, false)
				.addCriterion("sprinkler", InventoryChangeTrigger.TriggerInstance.hasItems(IcyIncitement.SNOWBALL_SPRINKLER))
				.save(consumer, IcyIncitement.id("sprinkler").toString());
	}

	protected @Override void generateRecipes(MRecipeProvider provider, HolderLookup.@NotNull @NonNull Provider registryLookup, RecipeOutput exporter) {
		var lookup = registryLookup.lookupOrThrow(Registries.ITEM);
		ShapedRecipeBuilder.shaped(lookup, RecipeCategory.TOOLS, IcyIncitement.EMPTY_SPRINKLER)
				.pattern("I  ")
				.pattern("ND ")
				.pattern(" SD")
				.define('D', Items.DIAMOND)
				.define('I', Items.IRON_INGOT)
				.define('N', Items.IRON_NUGGET)
				.define('S', Items.STICK)
				.unlockedBy("has_diamond", RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(lookup, Items.DIAMOND)))
				.save(exporter);
	}
}