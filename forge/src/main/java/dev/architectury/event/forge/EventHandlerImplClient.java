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

package dev.architectury.event.forge;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.*;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.impl.ScreenAccessImpl;
import dev.architectury.impl.TooltipEventColorContextImpl;
import dev.architectury.impl.TooltipEventPositionContextImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class EventHandlerImplClient {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ItemTooltipEvent event) {
        ClientTooltipEvent.ITEM.invoker().append(event.getItemStack(), event.getToolTip(), event.getFlags());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase == net.minecraftforge.event.TickEvent.Phase.START)
            ClientTickEvent.CLIENT_PRE.invoker().tick(Minecraft.getInstance());
        else if (event.phase == net.minecraftforge.event.TickEvent.Phase.END)
            ClientTickEvent.CLIENT_POST.invoker().tick(Minecraft.getInstance());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
            ClientGuiEvent.RENDER_HUD.invoker().renderHud(event.getMatrixStack(), event.getPartialTicks());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.invoker().join(event.getPlayer());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.invoker().quit(event.getPlayer());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientPlayerNetworkEvent.RespawnEvent event) {
        ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.invoker().respawn(event.getOldPlayer(), event.getNewPlayer());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (ClientGuiEvent.INIT_PRE.invoker().init(event.getGui(), new ScreenAccessImpl(event.getGui())).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.InitGuiEvent.Post event) {
        ClientGuiEvent.INIT_POST.invoker().init(event.getGui(), new ScreenAccessImpl(event.getGui()));
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getInstance().options.renderDebug) {
            ClientGuiEvent.DEBUG_TEXT_LEFT.invoker().gatherText(event.getLeft());
            ClientGuiEvent.DEBUG_TEXT_RIGHT.invoker().gatherText(event.getRight());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(net.minecraftforge.client.event.ClientChatEvent event) {
        CompoundEventResult<String> process = ClientChatEvent.PROCESS.invoker().process(event.getMessage());
        if (process.isPresent()) {
            if (process.isFalse())
                event.setCanceled(true);
            else if (process.object() != null)
                event.setMessage(process.object());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientChatReceivedEvent event) {
        CompoundEventResult<Component> process = ClientChatEvent.RECEIVED.invoker().process(event.getType(), event.getMessage(), event.getSenderUUID());
        if (process.isPresent()) {
            if (process.isFalse())
                event.setCanceled(true);
            else if (process.object() != null)
                event.setMessage(process.object());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(WorldEvent.Load event) {
        if (event.getWorld().isClientSide()) {
            ClientLevel world = (ClientLevel) event.getWorld();
            ClientLifecycleEvent.CLIENT_LEVEL_LOAD.invoker().act(world);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiOpenEvent event) {
        CompoundEventResult<Screen> result = ClientGuiEvent.SET_SCREEN.invoker().modifyScreen(event.getGui());
        if (result.isPresent()) {
            if (result.isFalse())
                event.setCanceled(true);
            else if (result.object() != null)
                event.setGui(result.object());
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (ClientGuiEvent.RENDER_PRE.invoker().render(event.getGui(), event.getMatrixStack(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.DrawScreenEvent.Post event) {
        ClientGuiEvent.RENDER_POST.invoker().render(event.getGui(), event.getMatrixStack(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(PlayerInteractEvent.RightClickEmpty event) {
        InteractionEvent.CLIENT_RIGHT_CLICK_AIR.invoker().click(event.getPlayer(), event.getHand());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(PlayerInteractEvent.LeftClickEmpty event) {
        InteractionEvent.CLIENT_LEFT_CLICK_AIR.invoker().click(event.getPlayer(), event.getHand());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(RecipesUpdatedEvent event) {
        ClientRecipeUpdateEvent.EVENT.invoker().update(event.getRecipeManager());
    }
    
    private static final ThreadLocal<TooltipEventColorContextImpl> tooltipColorContext = ThreadLocal.withInitial(TooltipEventColorContextImpl::new);
    private static final ThreadLocal<TooltipEventPositionContextImpl> tooltipPositionContext = ThreadLocal.withInitial(TooltipEventPositionContextImpl::new);
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(RenderTooltipEvent.Pre event) {
        if (ClientTooltipEvent.RENDER_FORGE_PRE.invoker().renderTooltip(event.getMatrixStack(), event.getLines(), event.getX(), event.getY()).isFalse()) {
            event.setCanceled(true);
            return;
        }
        
        TooltipEventPositionContextImpl positionContext = tooltipPositionContext.get();
        positionContext.reset(event.getX(), event.getY());
        ClientTooltipEvent.RENDER_MODIFY_POSITION.invoker().renderTooltip(event.getMatrixStack(), positionContext);
        event.setX(positionContext.getTooltipX());
        event.setY(positionContext.getTooltipY());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(RenderTooltipEvent.Color event) {
        TooltipEventColorContextImpl colorContext = tooltipColorContext.get();
        colorContext.reset();
        colorContext.setBackgroundColor(event.getBackground());
        colorContext.setOutlineGradientTopColor(event.getBorderStart());
        colorContext.setOutlineGradientBottomColor(event.getBorderEnd());
        ClientTooltipEvent.RENDER_MODIFY_COLOR.invoker().renderTooltip(event.getMatrixStack(), event.getX(), event.getY(), colorContext);
        event.setBackground(colorContext.getBackgroundColor());
        event.setBorderEnd(colorContext.getOutlineGradientBottomColor());
        event.setBorderStart(colorContext.getOutlineGradientTopColor());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseScrollEvent.Pre event) {
        if (ClientScreenInputEvent.MOUSE_SCROLLED_PRE.invoker().mouseScrolled(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getScrollDelta()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseScrollEvent.Post event) {
        ClientScreenInputEvent.MOUSE_SCROLLED_POST.invoker().mouseScrolled(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getScrollDelta());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (ClientScreenInputEvent.MOUSE_CLICKED_PRE.invoker().mouseClicked(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseClickedEvent.Post event) {
        ClientScreenInputEvent.MOUSE_CLICKED_POST.invoker().mouseClicked(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseDragEvent.Pre event) {
        if (ClientScreenInputEvent.MOUSE_DRAGGED_PRE.invoker().mouseDragged(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseDragEvent.Post event) {
        ClientScreenInputEvent.MOUSE_DRAGGED_POST.invoker().mouseDragged(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (ClientScreenInputEvent.MOUSE_RELEASED_PRE.invoker().mouseReleased(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.MouseReleasedEvent.Post event) {
        ClientScreenInputEvent.MOUSE_RELEASED_PRE.invoker().mouseReleased(Minecraft.getInstance(), event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.KeyboardCharTypedEvent.Pre event) {
        if (ClientScreenInputEvent.CHAR_TYPED_PRE.invoker().charTyped(Minecraft.getInstance(), event.getGui(), event.getCodePoint(), event.getModifiers()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.KeyboardCharTypedEvent.Post event) {
        ClientScreenInputEvent.CHAR_TYPED_POST.invoker().charTyped(Minecraft.getInstance(), event.getGui(), event.getCodePoint(), event.getModifiers());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (ClientScreenInputEvent.KEY_PRESSED_PRE.invoker().keyPressed(Minecraft.getInstance(), event.getGui(), event.getKeyCode(), event.getScanCode(), event.getModifiers()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        ClientScreenInputEvent.KEY_PRESSED_POST.invoker().keyPressed(Minecraft.getInstance(), event.getGui(), event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        if (ClientScreenInputEvent.KEY_RELEASED_PRE.invoker().keyReleased(Minecraft.getInstance(), event.getGui(), event.getKeyCode(), event.getScanCode(), event.getModifiers()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(GuiScreenEvent.KeyboardKeyReleasedEvent.Post event) {
        ClientScreenInputEvent.KEY_RELEASED_POST.invoker().keyReleased(Minecraft.getInstance(), event.getGui(), event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(InputEvent.MouseScrollEvent event) {
        if (ClientRawInputEvent.MOUSE_SCROLLED.invoker().mouseScrolled(Minecraft.getInstance(), event.getScrollDelta()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(InputEvent.RawMouseEvent event) {
        if (ClientRawInputEvent.MOUSE_CLICKED_PRE.invoker().mouseClicked(Minecraft.getInstance(), event.getButton(), event.getAction(), event.getMods()).isFalse()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(InputEvent.MouseInputEvent event) {
        ClientRawInputEvent.MOUSE_CLICKED_POST.invoker().mouseClicked(Minecraft.getInstance(), event.getButton(), event.getAction(), event.getMods());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(InputEvent.KeyInputEvent event) {
        ClientRawInputEvent.KEY_PRESSED.invoker().keyPressed(Minecraft.getInstance(), event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class ModBasedEventHandler {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void event(TextureStitchEvent.Pre event) {
            ClientTextureStitchEvent.PRE.invoker().stitch(event.getMap(), event::addSprite);
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void event(TextureStitchEvent.Post event) {
            ClientTextureStitchEvent.POST.invoker().stitch(event.getMap());
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void event(FMLClientSetupEvent event) {
            ClientLifecycleEvent.CLIENT_SETUP.invoker().stateChanged(Minecraft.getInstance());
        }
    }
}
