/*
 * This file is part of architectury.
 * Copyright (C) 2020, 2021 architectury
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package dev.architectury.hooks.client.screen.forge;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ScreenHooksImpl {
    public static List<NarratableEntry> getNarratables(Screen screen) {
        return screen.narratables;
    }
    
    public static List<Widget> getRenderables(Screen screen) {
        return screen.renderables;
    }
    
    public static <T extends AbstractWidget & Widget & NarratableEntry> T addRenderableWidget(Screen screen, T widget) {
        try {
            return (T) ObfuscationReflectionHelper.findMethod(Screen.class, "m_142416_", GuiEventListener.class).invoke(screen, widget);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T extends Widget> T addRenderableOnly(Screen screen, T listener) {
        try {
            return (T) ObfuscationReflectionHelper.findMethod(Screen.class, "m_169394_", Widget.class).invoke(screen, listener);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T extends GuiEventListener & NarratableEntry> T addWidget(Screen screen, T listener) {
        try {
            return (T) ObfuscationReflectionHelper.findMethod(Screen.class, "m_7787_", GuiEventListener.class).invoke(screen, listener);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
