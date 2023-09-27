package ezen.chat.server;

import java.io.IOException;

/**
 * 서버 실행 애플리케이션(2023-05-31)
 * 
 * @author 이희영
 */
public class EzenTalkServer {

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		
		try {
			chatServer.startup();
		} catch (IOException e) {
			System.out.println("ChatServer 실행 중 예외 발생");
			System.out.println("-" + e.getMessage());
		}
	}
}