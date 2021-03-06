package ru.betterend.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import ru.betterend.util.TagHelper;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {
	@Inject(method = "applyReload", at = @At(value = "HEAD"))
	private void onReload(Map<Identifier, Tag.Builder> tags, CallbackInfoReturnable<TagGroup<?>> info) {
		tags.forEach((id, builder) -> {
			TagHelper.apply(id, builder);
		});
	}
}
