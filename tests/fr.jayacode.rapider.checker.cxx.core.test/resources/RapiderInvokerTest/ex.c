#include <stdio.h>  /*@suppress("Problèmes Rapider")*/
#include <stdlib.h>
#include <string.h>
#include <alloca.h>

void
ex1(int index, const char *indent)
{
  int n = 0;
  char *str = (char *)alloca(index + strlen(indent) +1);
  memset(str, 0, index + strlen(indent) +1);
  for (n = 0; n < index; n++) str[n] = ' ';
  printf("%sat column %d: %s\n", str, index, indent);
}

int main(int argc, char **argv)
{
	int i = 2;
	i = i;
  ex1(5, "test ex1");
  ex1(14, "ex2 : tested");
}
