package ezen.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import ezen.chat.protocol.MessageType;

/**
 * 채팅 서버 (2023-05-31)
 * 
 * @author 이희영
 */
public class ChatServer {

	private static final int SERVER_PORT = 7777;
	private ServerSocket serverSocket;
	private boolean running;
	private Map<String, ChatHandler> clients;

	/** ChatServer 구동 */
	public void startup() throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		System.out.println("========= [ChatServer(" + SERVER_PORT + ")] Start =========");
		running = true;
		clients = new Hashtable<String, ChatHandler>();
		try {
			while (running) {
				System.out.println("[ChatServer(" + SERVER_PORT + ")] ChatClient Connect Listenning ..");
				Socket socket = serverSocket.accept();
				System.out.println("[ChatClient(" + socket.getInetAddress().getHostAddress() + ")] 연결해옴...");

				// 데이터 송수신 스레드 생성 및 실행
				ChatHandler chatHandler = new ChatHandler(socket, this);
				chatHandler.start();
			}
		} catch (IOException e) {
			System.err.println("[ChatServer(" + SERVER_PORT + ")] 실행 중 아래와 같은 오류가 발생하였습니다.");
			System.err.println("오류 내용 :  " + e.getMessage());
		}
	}

	/** 접속한 클라이언트를 콜렉션에 추가 */
	public void addChatClient(ChatHandler chatHandler) {
		clients.put(chatHandler.getNickName(), chatHandler);
		System.out.println("[현재 채팅에 참여중인 클라이언트 수] : " + clients.size());
	}

	/** 접속한 클라이언트를 콜렉션에 제거 */
	public void removeChatClient(ChatHandler chatHandler) {
		clients.remove(chatHandler.getNickName());
		System.out.println("[현재 채팅에 참여중인 클라이언트 수] : " + clients.size());
	}

	/**
	 * 접속한 모든 클라이언트에게 메시지 전송
	 * 
	 * @throws IOException
	 */
	public void sendMessageAll(String message) throws IOException {
		Collection<ChatHandler> list = clients.values();
		for (ChatHandler chatHandler : list) {
			chatHandler.sendMessage(message);
		}
	}

	/**
	 * DM 메세지 전송 (2023-06-01)
	 * 
	 * @param message    메세지
	 * @param toNickName 받는 사람
	 * @throws IOException
	 */
	public void sendDmMessage(String message) throws IOException {
		String[] tokens = message.split("\\|");
		String clientName = tokens[1];
		String toNickName = tokens[2];

		Collection<ChatHandler> list = clients.values();
		for (ChatHandler chatHandler : list) {
			if (chatHandler.getNickName().equals(toNickName) || chatHandler.getNickName().equals(clientName)) {
				chatHandler.sendMessage(message);
			}
			System.out.println(message);
		}
	}

	/**
	 * 채팅 접속자 리스트 메세지 전송 (2023-06-01)
	 * 
	 * @throws IOException
	 */
	public void sendClientList(String message) throws IOException {
		String[] tokens = message.split("\\|");
		String clientName = tokens[1] + "|";
		String token = MessageType.CLIENT_LIST + "|";
		String clientList = token + clientName;

		Collection<ChatHandler> list = clients.values();
		for (ChatHandler chatHandler : list) {
			String nickName = chatHandler.getNickName();
			clientList += nickName + ",";
		}
		clientList = clientList.substring(0, clientList.length() - 1);
		sendMessageAll(clientList);
	}

	/** ChatServer 종료 */
	public void shutdown() {
		try {
			serverSocket.close();
			System.out.println("[ChatServer(" + SERVER_PORT + ")] 종료됨...");
		} catch (IOException e) {}
	}
}