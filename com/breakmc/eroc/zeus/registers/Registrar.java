package com.breakmc.eroc.zeus.registers;

public interface Registrar
{
    void registerCommand(String p0, Object p1);
    
    void registerAll(Object p0);
    
    void registerAllSubCommands(Object p0);
    
    void registerSubCommand(Object p0, String p1);
}
