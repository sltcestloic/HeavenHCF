package fr.taeron.hcf.combatlog;

public final class NpcNameGeneratorFactory
{
    private static NpcNameGenerator nameGenerator;
    
    public static NpcNameGenerator getNameGenerator() {
        return NpcNameGeneratorFactory.nameGenerator;
    }
    
    public static void setNameGenerator(final NpcNameGenerator nameGenerator) {
        NpcNameGeneratorFactory.nameGenerator = nameGenerator;
    }
}
