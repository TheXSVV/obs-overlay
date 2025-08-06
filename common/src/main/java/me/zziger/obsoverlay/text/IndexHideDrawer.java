package me.zziger.obsoverlay.text;

import com.google.common.collect.Lists;
import me.zziger.obsoverlay.accessor.TextRendererInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.List;

// Декомпилированный Drawer с изменениями
public class IndexHideDrawer implements CharacterVisitor {

    private final TextRenderer textRenderer;
    private final int hideIndex;
    private final int rankValueStart;
    private final int anarchyDigitsStart;

    final VertexConsumerProvider vertexConsumers;
    private final boolean shadow;
    private final float brightnessMultiplier;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final Matrix4f matrix;
    private final TextRenderer.TextLayerType layerType;
    private final int light;
    float x;
    float y;
    @Nullable
    private List<GlyphRenderer.Rectangle> rectangles;

    private void addRectangle(GlyphRenderer.Rectangle rectangle) {
        if (this.rectangles == null) {
            this.rectangles = Lists.newArrayList();
        }

        this.rectangles.add(rectangle);
    }

    public IndexHideDrawer(final TextRenderer textRenderer, final int hideIndex, final int rankValueStart, final int anarchyDigitsStart, final VertexConsumerProvider vertexConsumers, final float x, final float y, final int color, final boolean shadow, final Matrix4f matrix, final TextRenderer.TextLayerType layerType, final int light) {
        this.textRenderer = textRenderer;
        this.hideIndex = hideIndex;
        this.rankValueStart = rankValueStart;
        this.anarchyDigitsStart = anarchyDigitsStart;
        this.vertexConsumers = vertexConsumers;
        this.x = x;
        this.y = y;
        this.shadow = shadow;
        this.brightnessMultiplier = shadow ? 0.25F : 1.0F;
        this.red = (float)(color >> 16 & 255) / 255.0F * this.brightnessMultiplier;
        this.green = (float)(color >> 8 & 255) / 255.0F * this.brightnessMultiplier;
        this.blue = (float)(color & 255) / 255.0F * this.brightnessMultiplier;
        this.alpha = (float)(color >> 24 & 255) / 255.0F;
        this.matrix = matrix;
        this.layerType = layerType;
        this.light = light;
    }

    private int index = 0;
    private int anarchyReplacementIndex = 0;
    private boolean inAnarchyReplacement = false;
    private int replacementIndex = 0;
    private int rankReplacementIndex = 0;
    private boolean inRankReplacement = false;

    private static final String REPLACEMENT_STRING = "Ксолвик";
    private static final String RANK_REPLACEMENT = "Staff";
    private static final String ANARCHY_REPLACEMENT = "000";

    public boolean accept(int i, Style style, int charInt) {
        TextRendererInterface rendererInterface = (TextRendererInterface) textRenderer;

        if (MinecraftClient.getInstance().player != null) {
            String playerName = MinecraftClient.getInstance().player.getName().getString();

            if (hideIndex != -1 && index >= hideIndex && index < hideIndex + playerName.length()) {
                if (replacementIndex < REPLACEMENT_STRING.length()) {
                    charInt = REPLACEMENT_STRING.charAt(replacementIndex);
                    replacementIndex++;
                } else {
                    index++;
                    return true;
                }
            } else if (rankValueStart != -1 && index >= rankValueStart && !Character.isWhitespace(charInt)) {
                if (!inRankReplacement) {
                    inRankReplacement = true;
                    rankReplacementIndex = 0;
                }

                if (rankReplacementIndex < RANK_REPLACEMENT.length()) {
                    charInt = RANK_REPLACEMENT.charAt(rankReplacementIndex);
                    rankReplacementIndex++;
                } else {
                    index++;
                    return true;
                }
            } else if (anarchyDigitsStart != -1 && index >= anarchyDigitsStart && index < anarchyDigitsStart + 3 && Character.isDigit(charInt)) {
                if (!inAnarchyReplacement) {
                    inAnarchyReplacement = true;
                    anarchyReplacementIndex = 0;
                }

                if (anarchyReplacementIndex < ANARCHY_REPLACEMENT.length()) {
                    charInt = ANARCHY_REPLACEMENT.charAt(anarchyReplacementIndex);
                    anarchyReplacementIndex++;
                } else {
                    index++;
                    return true;
                }
            }

            index++;
        }

        FontStorage fontStorage = rendererInterface.getFontStorageAccessor(style.getFont());
        Glyph glyph = fontStorage.getGlyph(charInt, rendererInterface.validateAdvanceAccessor());
        GlyphRenderer glyphRenderer = style.isObfuscated() && charInt != 32 ? fontStorage.getObfuscatedGlyphRenderer(glyph) : fontStorage.getGlyphRenderer(charInt);
        boolean bl = style.isBold();
        float f = this.alpha;
        TextColor textColor = style.getColor();
        float g;
        float h;
        float l;
        if (textColor != null) {
            int k = textColor.getRgb();
            g = (float)(k >> 16 & 255) / 255.0F * this.brightnessMultiplier;
            h = (float)(k >> 8 & 255) / 255.0F * this.brightnessMultiplier;
            l = (float)(k & 255) / 255.0F * this.brightnessMultiplier;
        } else {
            g = this.red;
            h = this.green;
            l = this.blue;
        }

        float n;
        float m;
        if (!(glyphRenderer instanceof EmptyGlyphRenderer)) {
            m = bl ? glyph.getBoldOffset() : 0.0F;
            n = this.shadow ? glyph.getShadowOffset() : 0.0F;
            VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(glyphRenderer.getLayer(this.layerType));
            rendererInterface.drawGlyphAccessor(glyphRenderer, bl, style.isItalic(), m, this.x + n, this.y + n, this.matrix, vertexConsumer, g, h, l, f, this.light);
        }

        m = glyph.getAdvance(bl);
        n = this.shadow ? 1.0F : 0.0F;
        if (style.isStrikethrough()) {
            this.addRectangle(new GlyphRenderer.Rectangle(this.x + n - 1.0F, this.y + n + 4.5F, this.x + n + m, this.y + n + 4.5F - 1.0F, 0.01F, g, h, l, f));
        }

        if (style.isUnderlined()) {
            this.addRectangle(new GlyphRenderer.Rectangle(this.x + n - 1.0F, this.y + n + 9.0F, this.x + n + m, this.y + n + 9.0F - 1.0F, 0.01F, g, h, l, f));
        }

        this.x += m;
        return true;
    }

    public float drawLayer(int underlineColor, float x) {
        TextRendererInterface rendererInterface = (TextRendererInterface) textRenderer;

        if (underlineColor != 0) {
            float f = (float)(underlineColor >> 24 & 255) / 255.0F;
            float g = (float)(underlineColor >> 16 & 255) / 255.0F;
            float h = (float)(underlineColor >> 8 & 255) / 255.0F;
            float i = (float)(underlineColor & 255) / 255.0F;
            this.addRectangle(new GlyphRenderer.Rectangle(x - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, g, h, i, f));
        }

        if (this.rectangles != null) {
            GlyphRenderer glyphRenderer = rendererInterface.getFontStorageAccessor(Style.DEFAULT_FONT_ID).getRectangleRenderer();
            VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(glyphRenderer.getLayer(this.layerType));
            Iterator var9 = this.rectangles.iterator();

            while(var9.hasNext()) {
                GlyphRenderer.Rectangle rectangle = (GlyphRenderer.Rectangle)var9.next();
                glyphRenderer.drawRectangle(rectangle, this.matrix, vertexConsumer, this.light);
            }
        }

        return this.x;
    }
}
