package it.smallcode.smallpets.cmds;
/*

Class created by SmallCode
02.07.2020 15:25

*/

import it.smallcode.smallpets.SmallPets;
import it.smallcode.smallpets.manager.types.User;
import it.smallcode.smallpets.pets.Pet;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SmallPetsCMD implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {

        if (args.length == 0) {

            if (s instanceof Player) {

                Player p = (Player) s;

                User user = SmallPets.getInstance().getUserManager().getUser(p.getUniqueId().toString());

                if (user != null) {

                    List<Pet> pets = user.getPets();

                    SmallPets.getInstance().getInventoryManager().openPetsMenu(pets, p);

                    p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);

                } else {

                    p.sendMessage(SmallPets.getInstance().PREFIX + "Nie moge znalezc tego gracza!");

                }

            } else {

                s.sendMessage(SmallPets.getInstance().PREFIX + "Nie można użyć tego w konsoli");

            }

            return false;

        } else if (args.length == 4) {

            if (args[0].equalsIgnoreCase("admin")) {

                if (s.hasPermission("smallpets.admin")) {

                    if (args[1].equalsIgnoreCase("givepet")) {

                        if (s.hasPermission("smallpets.admin.givepet") || s.hasPermission("smallpets.admin.*")) {

                            if (Bukkit.getPlayer(args[2]) != null && Bukkit.getPlayer(args[2]).isOnline()) {

                                SmallPets.getInstance().getUserManager().giveUserPet(args[3], Bukkit.getPlayer(args[2]).getUniqueId().toString());

                                s.sendMessage(SmallPets.getInstance().PREFIX + "Dodano zwierzaka " + args[3] + " graczowi " + args[2] + "!");

                                Bukkit.getPlayer(args[2]).sendMessage(SmallPets.getInstance().PREFIX + "Otrzymano zwierzaka " + args[3] + " od " + s.getName() + "!");

                                return false;

                            } else {

                                s.sendMessage(SmallPets.getInstance().PREFIX + "Gracz nie jest online!");

                                return false;

                            }

                        } else {

                            s.sendMessage(SmallPets.getInstance().PREFIX + "Brak permisji");

                            return false;

                        }

                    }

                } else {

                    s.sendMessage(SmallPets.getInstance().PREFIX + "Brak permisji");

                    return false;

                }

            }

            s.sendMessage(SmallPets.getInstance().PREFIX + "/zwierzaki");
            s.sendMessage(SmallPets.getInstance().PREFIX + "/zwierzaki admin dodaj <gracz> <nazwa>");

        }

        return false;

    }
}
