package utils

import "fmt"

type Config struct {
	Host string
	Port int
	Debug bool
}

func GetConfig() Config {
	config := Config{Host: "localhost", Port: 8080, Debug: true}
	return config
}

func PrintConfig(cfg Config) {
	fmt.Printf("Host: %s, Port: %d, Debug: %v\n", cfg.Host, cfg.Port, cfg.Debug)
}
