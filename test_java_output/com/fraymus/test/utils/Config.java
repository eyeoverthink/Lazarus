package com.fraymus.test.utils;

import java.util.*;
import java.io.*;

public class Config {

    public static class Config {
        public String Host;
        public int Port;
        public boolean Debug;
    }

    public static Config GetConfig() {
        var config = Config{Host: "localhost", Port: 8080, Debug: true}
        return config;
    }

    public static void PrintConfig(Config cfg) {
        System.out.printf("Host: %s, Port: %d, Debug: %v\n", cfg.Host, cfg.Port, cfg.Debug);
    }
}