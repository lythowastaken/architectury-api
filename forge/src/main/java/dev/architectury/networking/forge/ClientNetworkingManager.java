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

package dev.architectury.networking.forge;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientNetworkingManager {
    public static void initClient() {
        NetworkManagerImpl.CHANNEL.addListener(NetworkManagerImpl.createPacketHandler(NetworkEvent.ServerCustomPayloadEvent.class, NetworkManagerImpl.S2C));
        MinecraftForge.EVENT_BUS.register(ClientNetworkingManager.class);
        
        NetworkManagerImpl.registerS2CReceiver(NetworkManagerImpl.SYNC_IDS, (buffer, context) -> {
            Set<ResourceLocation> receivables = NetworkManagerImpl.serverReceivables;
            int size = buffer.readInt();
            receivables.clear();
            for (int i = 0; i < size; i++) {
                receivables.add(buffer.readResourceLocation());
            }
            NetworkManager.sendToServer(NetworkManagerImpl.SYNC_IDS, NetworkManagerImpl.sendSyncPacket(NetworkManagerImpl.C2S));
        });
    }
    
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
    
    @SubscribeEvent
    public static void loggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        NetworkManagerImpl.serverReceivables.clear();
    }
}
