package main

import "fmt"

type User struct {
	Name string
	Age  int
	Email string
}

func Add(a int, b int) int {
	return a + b
}

func Greet(name string) {
	fmt.Println("Hello, " + name)
}

func main() {
	x := 10
	y := 20
	result := Add(x, y)
	fmt.Println("Result:", result)
	
	user := User{Name: "Alice", Age: 30, Email: "alice@example.com"}
	Greet(user.Name)
	
	isValid := true
	if isValid {
		fmt.Println("Valid user")
	}
}
