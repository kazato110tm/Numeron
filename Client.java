//Client.java
import java. io.* ;
import java. net.* ;

public class Client{
	public static final int num_length = 3;
	
	public static boolean Search_str(String ans_num){		//����������
		boolean jud = true;
		if( ans_num.length() != num_length ){
			System.out.println("3���œ��͂��Ă�������");
			jud = false;
		}else{
			for(int i = 0 ; i < ans_num.length() ; i++ )
				if( ans_num.charAt(i) < '0' || ans_num.charAt(i) > '9' )	//�e��������
					jud = false;
			if( jud == false )
				System.out.println("��������͂��Ă�������");
		}
		if( jud == true ){									//�e���̂��Ԃ蔻��
			for(int i = 0 ; i < ans_num.length() ; i++ )
				for(int j = 0 ; j < ans_num.length() ; j++ )
					if( i != j )
						if( ans_num.charAt(i) == ans_num.charAt(j))
							jud = false;
			if( jud == false )
				System.out.println("�e���͔��Ȃ��悤�ɂ��Ă�������");
		}
		return jud;
	}
	
	//���C���֐�
	public static void main(String[] args){
		boolean game = true;
		byte player_num = -1;
		String address = args[0];							// �ڑ����IP�A�h���X
		int port = Integer.parseInt( args[1] );				// �ڑ���̃|�[�g�ԍ�
		
		Socket sock = null ;
		InputStream in = null ;
		OutputStream out = null ;
		
		try{
			sock = new Socket( address , port );		//port open
			in = sock.getInputStream();
			out = sock.getOutputStream();
			while( ( player_num = (byte)in.read() ) == -1 );	//�����̔ԍ��󂯎��
			if( player_num == 0 ){
				int invite;
				do{
					invite = 0;
					System.out.println("���l���҂��܂����H(�ő�4�l)");
					invite = System.in.read();
					invite = invite - '0';
				}while( invite > 4  && invite < 1 );
				out.write( (byte)invite );
				System.in.skip(5);
			}
			while(game){
				try{
					String ans_num = "";	//�񓚗p������
					String get_num = "";	//�󂯎��p������
					byte turn_pl = -1 ;
					byte mode = -1 ;
					byte end = -1 ;
					byte EAT = -1;
					byte BITE = -1;
					
					while( ( turn_pl = (byte)in.read() ) == -1 );	//�����̔ԍ��󂯎��
					mode = (byte)in.read();
					if( player_num == turn_pl ){					//�񓚃v���C���[�m�F
						System.out.println("Game now");
						
						boolean jud = false;
						try{
							while( jud == false ){
								if( mode == 0 )
									System.out.println("3���̐�����I��ł�������");
								else if( mode ==1 )
									System.out.println("3���̐�����\�z���Ă�������");
								
								BufferedReader input = new BufferedReader (new InputStreamReader (System.in));	//���������
								ans_num = input.readLine( );
								jud = Search_str(ans_num);		//��������
							}
							PrintWriter pw = new PrintWriter(out,true);
							pw.println(ans_num);
						}catch(IOException e){
							System.err.println("�l�b�g���[�N�̃G���[");
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
							System.err.println("�l�b�g���[�N�̃G���[");
							System.exit(1);
						}
					}
					while((end = (byte)in.read()) == -1 );
					if( end == 1 ){
						game = false;
						System.err.println("Player"+turn_pl+"�̏���");	//�����v���C���[�\��
					}
				}catch(IOException e){
					System.err.println("�l�b�g���[�N�̃G���[");
					System.exit(1);
				}
			}
			in.close();
			out.close();
		}catch(IOException e){
			System.err.println("�l�b�g���[�N�G���[");
			System.exit(1);
		}
	}
}
