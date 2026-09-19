package com.wayfinder.minecraft.command;
import com.mojang.brigadier.context.CommandContext;
import com.wayfinder.discovery.FirstDiscoverySequenceEvaluator;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataHistoricalEventRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import java.util.stream.Collectors;

public final class WayfinderDiscoveryStatusCommand {
 private WayfinderDiscoveryStatusCommand() {}
 public static int execute(CommandContext<CommandSourceStack> context) {
  var source=context.getSource(); var server=source.getLevel().getServer();
  var assessment=new FirstDiscoverySequenceEvaluator().assess(
    new MinecraftSavedDataCivilizationRepository(server).load(),
    new MinecraftSavedDataMaterializationRepository(server).load(),
    new MinecraftSavedDataHistoricalEventRepository(server).load());
  var beats=assessment.beats().stream().map(Enum::name).collect(Collectors.joining(", "));
  source.sendSuccess(()->Component.literal("Wayfinder discovery status: "+
    (assessment.coherent()?"COHERENT":"INCOMPLETE")+" | evidence ["+beats+"] | "+assessment.summary()),false);
  return 1;
 }
}
