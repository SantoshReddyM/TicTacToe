package com.game.ttt;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/*
 * Server end point which serves the responses for each request made by the client
 */
@ServerEndpoint("/play")
public class GameServer {

	private TicTacToe ttt;
    
	/*
	 * Acts as the controller for all the requests
	 */
	@OnMessage
	public void requestHandler(String message, Session session) throws IOException,
			InterruptedException {

		if (message.equals("computer")) {
			ttt.initialize();
			session.getBasicRemote().sendText("" + ttt.setComputerChoice());
			return;
		}
		else if (message.equals("user"))
		{
			ttt.initialize();
			return;
		}

		int userChoice = Integer.parseInt(message);

		String result = ttt.playTicTacToe(userChoice);

		session.getBasicRemote().sendText(result);

		if (ttt.isGameFinished()) {
			Player winner = ttt.getWinner();

			if (winner != null) {
				session.getBasicRemote().sendText(winner.getName());
			} else
				session.getBasicRemote().sendText("draw");
		}

	}

	@OnOpen
	public void onOpen() {
		System.out.println("conn opened");
		ttt = new TicTacToe();

	}

	@OnClose
	public void onClose() {
		System.out.println("Connection closed");
	}

}
