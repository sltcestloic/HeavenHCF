package fr.taeron.hcf.combatlog;

import java.util.*;
import org.bukkit.entity.*;

import fr.taeron.hcf.HCF;

;

public final class NpcNameGeneratorImpl implements NpcNameGenerator
{
    static final Random random;
    final HCF plugin;
    
    public NpcNameGeneratorImpl(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String generate(final Player player) {
        return player.getName();
    }
    
    static {
        random = new Random();
    }
}
