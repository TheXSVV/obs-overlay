package me.zziger.obsoverlay.accessor;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public interface TextRendererInterface {

    boolean validateAdvanceAccessor();

    FontStorage getFontStorageAccessor(Identifier id);

    void drawGlyphAccessor(GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light);
}
