# Architectural Description: T-Rex Endless Runner

## 1. Project Overview
This project is an independently engineered 2D endless runner simulation based on the Google Chrome dinosaur game. Rather than focusing on simple gameplay, this project was designed to build a custom, underlying game engine from scratch. It relies on mathematical coordinate tracking, procedural generation algorithms, simulated physics, and a strict organizational architecture to create a seamless, deterministic virtual environment.

---

## 2. Model-View-Controller (MVC) Architecture
To ensure the game is scalable and easy to maintain, the entire system is built upon the **Model-View-Controller (MVC)** design pattern. This is a structural philosophy that divides the software into three distinct, interconnected components.



[Image of Model-View-Controller architecture diagram]


* **The Model (The Brain & Memory):** This component represents the pure data and rules of the game. The `Dinosaur` and `Obstacle` models do not know what a computer screen is, nor do they know how to draw themselves. They strictly hold mathematical data: X and Y coordinates, widths, heights, and speed.
* **The View (The Eyes & Canvas):** This is the visual representation of the game. The View doesn't know *why* the dinosaur is in the air, nor does it calculate gravity. It simply looks at the Model's current coordinates and paints the correct picture at that exact spot on the screen.
* **The Controller (The Muscle & Nervous System):** This is the master coordinator. The Controller listens to the player's keyboard. When you press the spacebar, the Controller tells the Model to update its jump velocity. It then runs the physics calculations and finally tells the View to erase the screen and redraw everything in their new positions.

By keeping these three roles completely separated, the system prevents "spaghetti code"—meaning we could completely swap out the graphics without ever accidentally breaking the physics.

---

## 3. Files and Folder Architecture
Reflecting the MVC design pattern, the project's internal files are highly organized into distinct folders (called "packages") based on their specific responsibilities.

* **`constant` (The Rulebook):** Contains the fixed, unchanging laws of the game universe. If we want to make gravity heavier, change the window size, or track whether the game is `PLAYING` or `GAME_OVER`, those universal settings are stored here so every other file can reference them.
* **`model` (The Blueprints):** Contains the data structures for anything that exists in the game world. It includes a foundational `Entity` blueprint that guarantees everything has a mathematical "hitbox," alongside the specific `Dinosaur`, `Cactus`, and `Pterodactyl` data trackers.
* **`core` (The Mechanics):** Houses the active engine controllers. This is where the master Game Loop lives, where keyboard inputs are translated into game actions, and where the collision detector runs its overlapping math.
* **`view` (The Presentation):** Contains the files dedicated entirely to the user interface. It holds the logic for popping open a desktop window, cropping images from the sprite sheet, and drawing colors onto the screen.

---

## 4. The Engine Heartbeat: The Game Loop
At the core of the `core` package is the **Game Loop**. A video game is essentially a rapid sequence of still images and mathematical updates. The engine runs on an isolated background worker thread that is strictly timed to execute a complete update cycle exactly 60 times per second (60 Frames Per Second).

During every single fraction of a second, the loop does three things in this exact order:
1.  **Reads Inputs:** Checks if the player is holding down a jump or duck key.
2.  **Updates State:** Moves obstacles to the left, applies gravity to the dinosaur, and spawns new hazards.
3.  **Renders Graphics:** Tells the View to erase the old screen and draw the new positions of all objects.

By locking this loop to a specific time interval, the game ensures that it runs at the exact same speed regardless of whether it is being played on a slow laptop or a high-end desktop computer.

---

## 5. Simulated Physics and Coordinate Geometry
The game exists on a 2D mathematical grid consisting of an X-axis (horizontal) and a Y-axis (vertical).

### The Treadmill Effect
The game creates the illusion of forward momentum without the dinosaur ever actually moving forward. The dinosaur's horizontal X-coordinate is locked permanently to the left side of the screen. Instead, the ground lines, cacti, and birds are spawned on the far right and have their X-coordinates steadily subtracted every frame. This creates a "treadmill" effect where the world slides past the player.

### Virtual Gravity and Jump Arcs
Jumping in the game is not a simple "move up, then move down" command. It simulates actual physics using velocity and acceleration.
* When a jump is triggered, the engine applies a massive **negative force** to the dinosaur's vertical speed (moving up on a computer screen requires subtracting from the Y-coordinate).
* Every single frame, a constant **gravity value** is added back to that vertical speed.
* This means the dinosaur shoots up quickly, gradually slows down as gravity fights it at the peak of the jump, and then accelerates as it falls back to the earth, creating a perfect, natural-looking parabolic jump arc.

---

## 6. Collision Detection (Hitboxes)
To determine if the player has lost, the game uses a concept called **Axis-Aligned Bounding Boxes (AABB)**.
* The engine draws an invisible, mathematical rectangle (a "hitbox") strictly around the edges of the dinosaur, and similar boxes around every cactus and pterodactyl.
* 60 times a second, the collision utility loops through all active obstacles on the screen and compares their boundaries with the dinosaur's boundaries.
* If the engine detects that even one single pixel of the dinosaur's box is overlapping with an obstacle's box, it triggers a catastrophic collision flag, halting the game loop immediately.

---

## 7. Procedural Generation and Scaling Difficulty
The environment is not pre-designed; it builds itself infinitely using stochastic (randomized) algorithms.

* **Spawning Logic:** The engine monitors the distance between the newest hazard and the right edge of the screen. Once enough empty buffer space clears, the game essentially rolls a virtual pair of dice on every frame. If the dice hit a specific low probability, a new obstacle is created. Another random roll determines whether that obstacle will be a cactus or a flying pterodactyl, and yet another roll determines its exact flight altitude.
* **Dynamic Velocity Scaling:** To increase difficulty, the game monitors a continuously rising internal score. Whenever the score crosses specific thresholds, the master "speed multiplier" is increased. This higher speed is instantly applied to the treadmill environment, forcing the player to calculate their jump arcs faster.

---

## 8. Java Swing and the Graphics Pipeline
To actually display the game on a desktop, the project utilizes **Java Swing** and **Java AWT**—native toolkits built into Java used to create Graphical User Interfaces (GUIs).

### Windowing and Hardware Communication
Java Swing acts as the bridge between our game's math and your computer's operating system. It is responsible for creating the physical, un-resizable window frame on your desktop. Furthermore, Swing provides a "Listener" interface that actively monitors your physical hardware, instantly capturing when a physical keyboard key is pressed or released and handing that information directly to our Game Controller.

### Thread Safety (The Event Dispatch Thread)
Graphics and heavy physics math do not mix well if they try to run at the exact same time. Java Swing safely manages this by using a dedicated worker called the **Event Dispatch Thread (EDT)**. While our custom Game Loop calculates gravity in the background, the EDT handles all the drawing and button-pressing. Because these two workers operate independently, the game window never freezes or locks up while calculating complex collisions.

### Animation via Sprite Cropping & Double Buffering
* **Sprite Cropping:** Instead of loading dozens of separate image files for every possible animation frame, the engine loads one master "atlas" image. Swing uses precise coordinates to act as a cookie-cutter, slicing out just the specific frame it needs (like a dinosaur with its left foot raised) and oscillating between frames to create movement.
* **Double Buffering:** To prevent the monitor from violently flickering while the graphics are being drawn, Swing paints the entire scene onto an invisible digital canvas hidden in the computer's memory. Once the drawing is 100% complete, it instantly swaps it onto the player's screen, ensuring a buttery-smooth visual presentation.