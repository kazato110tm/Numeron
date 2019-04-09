//Server.java
import java.io.*;
import java.net.*;

class Server{
	public static final int Port = 6001;
	public static final int Max_Client = 5;
	public static final int num_length = 3;

	public static void GetStream( Socket[] sock , InputStream[] in , OutputStream[] out , int n ){//�ڑ�����
		try{
			out[n] = sock[n].getOutputStream();
			in[n] = sock[n].getInputStream();
		}catch(IOException e){
			System.err.println( "ERROR getStream" );
			System.exit(1);
		}
	}
	
	
	public static byte Change_pl( byte turn , byte invite_pl ){	//�v���C���[�̃^�[���ύX
		turn ++;
		turn =(byte)(turn % invite_pl);
		if( turn == 0 )
			turn++ ;
		return (turn);
	}
	
	public static void CloseStream( InputStream[] in , OutputStream[] out , byte invite_pl ){		//�eStream�����
		try{
			for(int n = 0 ; n < invite_pl ; n++ ){
				in[n].close();
				out[n].close();
			}
		}catch(IOException e){
			System.err.println( "ERROR CloseStream" );
			System.exit(1);
		}
	}
	
	public static void CloseSock( Socket[] sock , byte invite_pl ){		//�\�P�b�g�����
		try{
			for(int n = 0 ; n < invite_pl ; n++ )
				sock[n].close();
		}catch(IOException e){
			System.err.println( "ERROR Closesock" );
			System.exit(1);
		}
	}
	
	
	public static void main(String[] args){
		ServerSocket servsock = null;
		Socket sock[] = { null , null , null , null , null};
		InputStream in[] = { null , null , null , null , null };
		OutputStream out[] = { null , null , null , null , null };
		boolean game = true ;
		boolean decide = false ;
		byte n=0;
		byte turn = 0 ;
		byte mode = 0 ;
		byte end = 0;
		byte invite_pl = 1;
		
		String ans_num = "";
		String answer = "" ;
		
		try{		//�ڑ�����
			servsock = new ServerSocket( Port , Max_Client );
			
			for( n = 0 ; n < invite_pl ; n++ ){
				sock[n] = servsock.accept();
				GetStream( sock , in , out , n );
				out[n].write(n);
				if( n == 0 ){
					while( ( invite_pl = (byte)in[n].read() ) == -1 );
					invite_pl++;			//���Ґ� + �I�[�i�[
				}
			}
			
			while( game ){//�Q�[�����̃��[�v
				byte EAT = 0;
				byte BITE = 0;
				for( n = 0 ; n < invite_pl ; n++){
					out[n].write(turn);			//�v���C���[�̃^�[���𑗐M
					out[n].write(mode);			//���͂̃��[�h�𑗐M	0:�ŏ��̒l����	1:��
				}
				BufferedReader br = new BufferedReader( new InputStreamReader(in[turn]));
				ans_num = br.readLine();
				System.out.println(ans_num);
				if( mode == 0 ){
					mode = 1;
					answer = ans_num;
					System.out.println( "������"+answer );
				}
				else{					//EAT , BITE ����
					decide = false ;
					EAT = 0;
					BITE = 0;
					for(int i = 0 ; i < num_length ; i++ ){
						Character a = new Character(ans_num.charAt(i));
						if(a != null )
						for(int j = 0 ; j < num_length ; j++ ){
							Character b = new Character(answer.charAt(j));
							if( (a.equals(b)) == true ){
								if( i == j )
									EAT++;
								else
									BITE++;
							}
						}
					}
					if( EAT == num_length )
						decide = true ;
					System.out.println( "EAT:"+EAT+" BITE:"+BITE );
					for( n = 0 ; n < invite_pl ; n++ ){
						out[n].write( EAT );
						out[n].write( BITE );
					}
				}
				if( turn > 0 )
					for( n = 0 ; n < invite_pl ; n++ ){	//�e�v���C���[�ւ̒l�̑��M
						PrintWriter pw = new PrintWriter(out[n],true);
						pw.println(ans_num);
					}
				System.out.println(decide);
				if(decide == true )	//�I������
					game = false;
				System.out.println("\nplayer change");
				turn = Change_pl( turn , invite_pl );
				if( game == false )
					end = 1;
				for( n = 0 ; n < invite_pl ; n++ )
					out[n].write( end );
			}
			CloseStream(in , out , invite_pl );
		}catch(IOException e){
			System.exit(1);
		}finally{
			CloseSock(sock , invite_pl );
		}
	}
}
