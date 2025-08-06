package me.zziger.obsoverlay;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

public class OverlayUtils {

    public static boolean isScreenOverlayed(Screen screen) {
        return screen instanceof ChatScreen;
    }
}
