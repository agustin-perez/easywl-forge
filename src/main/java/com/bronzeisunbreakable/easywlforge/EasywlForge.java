package com.bronzeisunbreakable.easywlforge;

import com.bronzeisunbreakable.easywlforge.commands.EasyWhitelistCommand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EasywlForge.MODID)
public class EasywlForge {
    public static final String MODID = "easywlforge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EasywlForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }


    /**
     * Método que se ejecuta cuando el server inicia
     *
     * @param event Evento disparado por el inicio del server
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[EasyWhitelist] El server utilizará whitelist basada en nombres, utiliza el comando wl");
        EasyWhitelistCommand.registerCommand(event.getServer().getCommands().getDispatcher());
    }

    /**
     * Client-side event subscriber for EasyWhitelist mod.
     * <p>
     * This class listens to events related to the client setup phase of the mod and performs any
     * necessary client-specific initialization tasks. It is annotated with {@link Mod.EventBusSubscriber}
     * to ensure that the event bus properly picks up the events related to the client lifecycle.
     * <p>
     * The {@link #onClientSetup(FMLClientSetupEvent)} method is called when the client is setting up,
     * which is the appropriate place to register client-only components such as rendering setups,
     * GUI elements, or input handlers.
     * </p>
     */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        /**
         * Called during the client setup phase of the mod's initialization.
         * <p>
         * This method is triggered by the {@link FMLClientSetupEvent} and is used to perform client-side
         * setup tasks for the mod. Typically, this is where you would register client-only features such as:
         * <ul>
         *   <li>Custom GUI screens</li>
         *   <li>Client-side rendering behavior</li>
         *   <li>Input handling (keybindings)</li>
         * </ul>
         * </p>
         * <p>
         * This method logs a message to indicate that the EasyWhitelist mod has been set up on the client.
         * </p>
         *
         * @param event The client setup event. Provides hooks for initializing client-specific features.
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("[EasyWhitelist] Whitelist configurada");
        }
    }
}
