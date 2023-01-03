package net.minecraft.server.command.handler;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AchievementCommand;
import net.minecraft.server.command.BlockDataCommand;
import net.minecraft.server.command.ClearCommand;
import net.minecraft.server.command.CloneCommand;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.DebugCommand;
import net.minecraft.server.command.DefaultGameModeCommand;
import net.minecraft.server.command.DifficultyCommand;
import net.minecraft.server.command.EffectCommand;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ExperienceCommand;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.HelpCommand;
import net.minecraft.server.command.ICommand;
import net.minecraft.server.command.KillCommand;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.command.PlaySoundCommand;
import net.minecraft.server.command.ReplaceItemCommand;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.command.SetWorldSpawnCommand;
import net.minecraft.server.command.SpawnPointCommand;
import net.minecraft.server.command.SpreadPlayersCommand;
import net.minecraft.server.command.StatsCommand;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.command.TellCommand;
import net.minecraft.server.command.TellRawCommand;
import net.minecraft.server.command.TestForBlockCommand;
import net.minecraft.server.command.TestForBlocksCommand;
import net.minecraft.server.command.TestForCommand;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.server.command.TitleCommand;
import net.minecraft.server.command.ToggleDownfallCommand;
import net.minecraft.server.command.TpCommand;
import net.minecraft.server.command.TriggerCommand;
import net.minecraft.server.command.WeatherCommand;
import net.minecraft.server.command.WorldBorderCommand;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.KickCommand;
import net.minecraft.server.dedicated.command.ListCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.server.integrated.command.PublishCommand;
import net.minecraft.text.Formatting;
import net.minecraft.text.TranslatableText;

public class CommandManager extends CommandRegistry implements CommandListener {
   public CommandManager() {
      this.register(new TimeCommand());
      this.register(new GameModeCommand());
      this.register(new DifficultyCommand());
      this.register(new DefaultGameModeCommand());
      this.register(new KillCommand());
      this.register(new ToggleDownfallCommand());
      this.register(new WeatherCommand());
      this.register(new ExperienceCommand());
      this.register(new TpCommand());
      this.register(new GiveCommand());
      this.register(new ReplaceItemCommand());
      this.register(new StatsCommand());
      this.register(new EffectCommand());
      this.register(new EnchantCommand());
      this.register(new ParticleCommand());
      this.register(new MeCommand());
      this.register(new SeedCommand());
      this.register(new HelpCommand());
      this.register(new DebugCommand());
      this.register(new TellCommand());
      this.register(new SayCommand());
      this.register(new SpawnPointCommand());
      this.register(new SetWorldSpawnCommand());
      this.register(new GameRuleCommand());
      this.register(new ClearCommand());
      this.register(new TestForCommand());
      this.register(new SpreadPlayersCommand());
      this.register(new PlaySoundCommand());
      this.register(new ScoreboardCommand());
      this.register(new ExecuteCommand());
      this.register(new TriggerCommand());
      this.register(new AchievementCommand());
      this.register(new SummonCommand());
      this.register(new SetBlockCommand());
      this.register(new FillCommand());
      this.register(new CloneCommand());
      this.register(new TestForBlocksCommand());
      this.register(new BlockDataCommand());
      this.register(new TestForBlockCommand());
      this.register(new TellRawCommand());
      this.register(new WorldBorderCommand());
      this.register(new TitleCommand());
      if (MinecraftServer.getInstance().isDedicated()) {
         this.register(new OpCommand());
         this.register(new DeOpCommand());
         this.register(new StopCommand());
         this.register(new SaveAllCommand());
         this.register(new SaveOffCommand());
         this.register(new SaveOnCommand());
         this.register(new BanIpCommand());
         this.register(new PardonIpCommand());
         this.register(new BanCommand());
         this.register(new BanListCommand());
         this.register(new PardonCommand());
         this.register(new KickCommand());
         this.register(new ListCommand());
         this.register(new WhitelistCommand());
         this.register(new SetIdleTimeoutCommand());
      } else {
         this.register(new PublishCommand());
      }

      Command.setListener(this);
   }

   @Override
   public void sendSuccess(CommandSource source, ICommand command, int flags, String message, Object... args) {
      boolean var6 = true;
      MinecraftServer var7 = MinecraftServer.getInstance();
      if (!source.sendCommandFeedback()) {
         var6 = false;
      }

      TranslatableText var8 = new TranslatableText("chat.type.admin", source.getName(), new TranslatableText(message, args));
      var8.getStyle().setColor(Formatting.GRAY);
      var8.getStyle().setItalic(true);
      if (var6) {
         for(PlayerEntity var10 : var7.getPlayerManager().players) {
            if (var10 != source && var7.getPlayerManager().isOp(var10.getGameProfile()) && command.canUse(source)) {
               var10.sendMessage(var8);
            }
         }
      }

      if (source != var7 && var7.worlds[0].getGameRules().getBoolean("logAdminCommands")) {
         var7.sendMessage(var8);
      }

      boolean var11 = var7.worlds[0].getGameRules().getBoolean("sendCommandFeedback");
      if (source instanceof CommandExecutor) {
         var11 = ((CommandExecutor)source).trackOutput();
      }

      if ((flags & 1) != 1 && var11) {
         source.sendMessage(new TranslatableText(message, args));
      }
   }
}
