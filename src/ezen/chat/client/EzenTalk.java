package ezen.chat.client;

/**
 * 채팅창 실행 애플리케이션 (2023-05-31)
 * 
 * @author 이희영
 */
public class EzenTalk {

	public static void main(String[] args) {
		ChatFrame chatFrame = new ChatFrame("::: 재밌는 대화 나누세요 :::");
		chatFrame.setSize(400, 500);
		chatFrame.init();
		chatFrame.addEventListener();
		chatFrame.setVisible(true);
	}
}