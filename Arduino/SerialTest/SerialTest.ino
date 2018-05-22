#include <MsTimer2.h>

#define TERMINATING_CHAR 10
#define MAX_CHAR 50

#define INPUT_PIN 2
#define LED_PIN 13

char BufferArray[MAX_CHAR];
String str;

int counter = 0;
bool is_push = false;

void flash() {
  counter++;
  Serial.println(counter);
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial.setTimeout(1000);

  pinMode(INPUT_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  MsTimer2::set(1000, flash); // 500ms period
  MsTimer2::start();
}

void loop() {

  if(digitalRead(INPUT_PIN) && is_push == false){
    is_push = true;
    Serial.println("release");
  }
  else if(!digitalRead(INPUT_PIN) && is_push == true){
    is_push = false;
    Serial.println("push");
  }

  if (Serial.available()) {
    for (int i = 0; i < MAX_CHAR; i++) {
      BufferArray[i] = 0;
    }
    Serial.readBytesUntil(TERMINATING_CHAR, BufferArray, MAX_CHAR);
    str = String(BufferArray);
    str.replace("\r", "");

    if (str == ("ON")) {
      digitalWrite(LED_PIN, HIGH);
    }
    if (str == ("OFF")) {
      digitalWrite(LED_PIN, LOW);
    }
  }
}
