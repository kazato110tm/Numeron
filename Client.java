//Client.java
import java. io.* ;
import java. net.* ;

public class Client{
	public static final int num_length = 3;
	
	public static boolean Search_str(String ans_num){		//文字長判定
		boolean jud = true;
		if( ans_num.length() != num_length ){
			System.out.println("3桁で入力してください");
			jud = false;
		}else{
			for(int i = 0 ; i < ans_num.length() ; i++ )
				if( ans_num.charAt(i) < '0' || ans_num.charAt(i) > '9' )	//各文字判定
					jud = false;
			if( jud == false )
				System.out.println("数字を入力してください");
		}
		if( jud == true ){									//各桁のかぶり判定
			for(int i = 0 ; i < ans_num.length() ; i++ )
				for(int j = 0 ; j < ans_num.length() ; j++ )
					if( i != j )
						if( ans_num.charAt(i) == ans_num.charAt(j))
							jud = false;
			if( jud == false )
				System.out.println("各桁は被らないようにしてください");
		}
		return jud;
	}
	
	//メイン関数
	public static void main(String[] args){
		boolean game = true;
		byte player_num = -1;
		String address = args[0];							// 接続先のIPアドレス
		int port = Integer.parseInt( args[1] );				// 接続先のポート番号
		
		Socket sock = null ;
		InputStream in = null ;
		OutputStream out = null ;
		
		try{
			sock = new Socket( address , port );		//port open
			in = sock.getInputStream();
			out = sock.getOutputStream();
			while( ( player_num = (byte)in.read() ) == -1 );	//自分の番号受け取り
			if( player_num == 0 ){
				int invite;
				do{
					invite = 0;
					System.out.println("何人招待しますか？(最大4人)");
					invite = System.in.read();
					invite = invite - '0';
				}while( invite > 4  && invite < 1 );
				out.write( (byte)invite );
				System.in.skip(5);
			}
			while(game){
				try{
					String ans_num = "";	//回答用文字列
					String get_num = "";	//受け取り用文字列
					byte turn_pl = -1 ;
					byte mode = -1 ;
					byte end = -1 ;
					byte EAT = -1;
					byte BITE = -1;
					
					while( ( turn_pl = (byte)in.read() ) == -1 );	//自分の番号受け取り
					mode = (byte)in.read();
					if( player_num == turn_pl ){					//回答プレイヤー確認
						System.out.println("Game now");
						
						boolean jud = false;
						try{
							while( jud == false ){
								if( mode == 0 )
									System.out.println("3桁の数字を選んでください");
								else if( mode ==1 )
									System.out.println("3桁の数字を予想してください");
								
								BufferedReader input = new BufferedReader (new InputStreamReader (System.in));	//文字列入力
								ans_num = input.readLine( );
								jud = Search_str(ans_num);		//文字判定
							}
							PrintWriter pw = new PrintWriter(out,true);
							pw.println(ans_num);
						}catch(IOException e){
							System.err.println("ネットワークのエラー");
							System.exit(1);
						}
					}
					if( turn_pl > 0 ){
						try{
							while( ( EAT = (byte)in.read() ) == -1 );
							BITE = (byte)in.read();
							BufferedReader br = new BufferedReader( new InputStreamReader(in));
							get_num = br.readLine();
							System.out.println( "Player" + turn_pl + ":" + get_num );
							
							System.out.println("EAT:"+EAT+" BITE:"+BITE);
						}catch(IOException e){
							System.err.println("ネットワークのエラー");
							System.exit(1);
						}
					}
					while((end = (byte)in.read()) == -1 );
					if( end == 1 ){
						game = false;
						System.err.println("Player"+turn_pl+"の勝利");	//勝利プレイヤー表示
					}
				}catch(IOException e){
					System.err.println("ネットワークのエラー");
					System.exit(1);
				}
			}
			in.close();
			out.close();
		}catch(IOException e){
			System.err.println("ネットワークエラー");
			System.exit(1);
		}
	}
}
