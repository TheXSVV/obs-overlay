package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.accessor.TextRendererInterface;
import me.zziger.obsoverlay.text.IndexHideDrawer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(TextRenderer.class)
public class TextRendererMixin implements TextRendererInterface {

    @Shadow
    @Final
    boolean validateAdvance;

    @Shadow
    FontStorage getFontStorage(Identifier id) {
        return null;
    }

    @Shadow
    void drawGlyph(GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {}

    @Inject(method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)F", at = @At("HEAD"), cancellable = true)
    private void drawLayer(
            OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextRenderer.TextLayerType layerType, int underlineColor, int light, CallbackInfoReturnable<Float> cir
    ) {
        TextRenderer self = (TextRenderer) (Object) this;

        StringBuilder stringBuilder = new StringBuilder();
        text.accept((i, style, charInt) -> {
            stringBuilder.append((char) charInt);
            return true;
        });

        String fullText = stringBuilder.toString();

        int nameIndex = -1;
        if (MinecraftClient.getInstance().player != null)
            nameIndex = fullText.indexOf(MinecraftClient.getInstance().player.getName().getString());

        int rankValueStart = -1;
        int rankPos = fullText.indexOf("Ранг:");
        if (rankPos != -1) {
            rankValueStart = rankPos + "Ранг:".length();
            while (rankValueStart < fullText.length() && fullText.charAt(rankValueStart) == ' ')
                rankValueStart++;
        }

        IndexHideDrawer drawer = new IndexHideDrawer(self, nameIndex, rankValueStart, vertexConsumerProvider, x, y, color, shadow, matrix, layerType, light);
        text.accept(drawer);
        cir.setReturnValue(drawer.drawLayer(underlineColor, x));
    }

    @Override
    public boolean validateAdvanceAccessor() {
        return validateAdvance;
    }

    @Override
    public FontStorage getFontStorageAccessor(Identifier id) {
        return getFontStorage(id);
    }

    @Override
    public void drawGlyphAccessor(GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {
        drawGlyph(glyphRenderer, bold, italic, weight, x, y, matrix, vertexConsumer, red, green, blue, alpha, light);
    }
}
