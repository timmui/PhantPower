int relayControl(String command);
int led = D7;
int relay = D0;
int turnOn(String command);
int turnOff(String command);
int getRelay(String command);
String one = String(1);
String zero = String(0);

void setup()
{
  // register the Spark function
  Spark.function("relayControl", relayControl);
  Spark.function("turnOn", turnOn);
  Spark.function("turnOff", turnOff);
  Spark.function("getRelay", getRelay);
  pinMode(relay, OUTPUT);
  pinMode(led, OUTPUT);
}

void loop()
{
  // this loops forever
}

// this function automagically gets called upon a matching POST request
int relayControl(String command)
{
  // look for the matching argument "coffee" <-- max of 64 characters long
  
  digitalWrite(relay, !(digitalRead(relay)));
  digitalWrite(led, !(digitalRead(led)));
  return 1;
}

int turnOn(String command)
{
    digitalWrite(relay, HIGH);   
    digitalWrite(led, HIGH); 
    return 1;
}

int turnOff(String command)
{
    digitalWrite(relay, LOW);   
    digitalWrite(led, LOW); 
    return 1;
}

int getRelay(String command)
{
    return digitalRead(relay);
}
