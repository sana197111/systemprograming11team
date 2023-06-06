#include <stdio.h>
#include <stdbool.h>  // 추가
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <cstdlib>


bool isPersonOnBed = true;
bool isPhoneConnected = true;
void turnOffLights() {
    printf("Turning off the lights.\n");
}
void checkPhoneUsage(char buffer) {//
    if (buffer==0x31) {
        printf("Phone is being used.\n");
    } else {
        printf("Phone is not being used.\n");
        turnOffLights();
    }
}




int main(int argc, char *argv[]) {
    struct addrinfo hints, *res;
    int sockfd, clientfd, status;
    char buffer[1024];

    // Set up socket parameters
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    
    // Resolve the IP address of the local host using getaddrinfo
    status = getaddrinfo(NULL, "5000", &hints, &res);
    if (status != 0) {
        fprintf(stderr, "getaddrinfo error: %s\n", gai_strerror(status));
        exit(1);
    }

    // Create a socket object and bind it to the local address
    sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
    if (sockfd == -1) {
        perror("socket error");
        exit(1);
    }

    int reuse = 1;
    setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof reuse);

    status = bind(sockfd, res->ai_addr, res->ai_addrlen);
    if (status == -1) {
        perror("bind error");
        exit(1);
    }

    // Listen for incoming connections
    status = listen(sockfd, 1);
    if (status == -1) {
        perror("listen error");
        exit(1);
    }
    


	// Accept a new connection
	printf("Listening on port 5000...\n");
	clientfd = accept(sockfd, NULL, NULL);
	if (clientfd == -1) {
		perror("accept error");
	}
	printf("Accepted connection from client\n");

	// Receive the message from the client

	printf("\n");
	while(1){
	// isisPersonOnBed?
	if (1){
	int bytes_received = recv(clientfd, buffer, sizeof buffer - 1, 0); //receive phonecheck
		buffer[bytes_received] = '\0';
		int temp = buffer[0];
		printf("Received message: %d\n", temp);
		//checkPhoneUsage(buffer[0]);
		// Send a response back to the client


		//light turn off
		
		if (buffer[0]=='1'){//isconected?
			printf("let's break\n");
			std::system("cd /home/pi/Adafruit_CircuitPython_AMG88xx/examples"); 
			std::system("python3 test.py"); 
			break;
		}
	
	}
	}

	printf("Sent response to client\n");

	// Close the socket connection
	send(clientfd, "3", 1, 0);
	close(clientfd);
	printf("Closed connection to client\n");
    

    // Free the address information
    freeaddrinfo(res);

    return 0;
}
