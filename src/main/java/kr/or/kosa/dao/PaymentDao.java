package kr.or.kosa.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.or.kosa.dto.Book;
import kr.or.kosa.dto.Book_Payment;
import kr.or.kosa.utils.ConnectionHelper;

public class PaymentDao implements BookMarkDao{
	
	

		public List<Book> cartlist(String id){ //장바구니 리스트
			Connection conn = ConnectionHelper.getConnection("oracle");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			List<Book> cartlist = null;
			
			try {
				String sql = "select book.isbn as isbn, book.book_name, book.author, book.description,book.price,book.book_filename,ebook.file_name from book"
						+ " left join ebook on book.isbn = ebook.isbn where book.isbn in (select isbn from cart where id =?)";
				//일단은 ebook파일까지 같이 담기도록 짰다.
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				cartlist = new ArrayList<Book>();
				while(rs.next()) {
					Book book = new Book();
					book.setIsbn(rs.getString("isbn"));
					book.setBook_name(rs.getString("book_name"));
					book.setAuthor(rs.getString("author"));
					book.setDescription(rs.getString("description"));
					book.setFile_name(rs.getString("file_name"));
					book.setPrice(rs.getInt("price"));
					book.setBook_filename(rs.getString("book_filename"));
					
					cartlist.add(book);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					ConnectionHelper.close(rs);
					ConnectionHelper.close(pstmt);
					ConnectionHelper.close(conn);
				} catch (Exception e2) {
					
				}
			}
			return cartlist;
		}
		
		//게시물 총 건수 구하기
				public int totalBoardCount() {
					Connection conn = null;
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					int totalcount = 0;
					try {
						conn = ConnectionHelper.getConnection("oracle"); //연결객체
						String sql = "select count(*) as cnt from question_board";
						pstmt = conn.prepareStatement(sql);
						rs = pstmt.executeQuery();
						if(rs.next()) {
							totalcount = rs.getInt("cnt");
						}
					} catch (Exception e) {
						System.out.println("totalBoardCount 예외 : " + e.getMessage());
					}
					return totalcount;
				}
		
		//장바구니에 책 추가하기
		public int AddBook(String id, String isbn) {
			Connection conn = ConnectionHelper.getConnection("oracle");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			int row = 0;
			
			try {
				String sql = "insert into cart(id, isbn) values(?,?)";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1,id);
				pstmt.setString(2,isbn);
				row = pstmt.executeUpdate();
				
			} catch (Exception e) {
				e.getStackTrace();
			}finally {
				try {
					ConnectionHelper.close(rs);
					ConnectionHelper.close(pstmt);
					ConnectionHelper.close(conn);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return row;
		}

		//public int deleteOk(String id, String isbn) { //장바구니 목록에서 지우기
		public int deleteOk(String id) { //전부 비우기
			Connection conn = ConnectionHelper.getConnection("oracle");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			int row = 0;
				
			try {
				String sql = "delete from cart where id=?";
				pstmt=conn.prepareStatement(sql);
				
				pstmt.setString(1, id);
				//pstmt.setString(2,isbn);
				row = pstmt.executeUpdate();
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					ConnectionHelper.close(rs);
					ConnectionHelper.close(pstmt);
					ConnectionHelper.close(conn);
				} catch (Exception e2) {
				
				}
			}
			
			return row;
		}
		//유저 한명의 결제목록
		public List<Book_Payment> paymentlist(String id){
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			List<Book_Payment> paymentlist = null;
			
			try {
				conn = ConnectionHelper.getConnection("oracle");
				String sql = "select book_payment.payment_no,isbn,count,to_char(payment_date),sumprice"
						+ " from book_payment join payment on book_payment.payment_no = payment.payment_no"
						+ " where payment.id = ? order by payment_no desc";
				System.out.println(sql);
				System.out.println(id);
				
				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				
				paymentlist = new ArrayList<Book_Payment>();
				while(rs.next()) {
					
					Book_Payment bookpayment = new Book_Payment();
					
					bookpayment.setPayment_no(rs.getString(1));					
					bookpayment.setIsbn(rs.getString(2));				
					bookpayment.setCount(rs.getInt(3));					
					bookpayment.setPayment_date(rs.getString(4));
					bookpayment.setSumprice(rs.getInt(5));
					paymentlist.add(bookpayment);
				}
				System.out.println(3);
			} catch (Exception e) {
				e.getStackTrace();
			}finally {
					ConnectionHelper.close(rs);
					ConnectionHelper.close(pstmt);
					ConnectionHelper.close(conn);
			}
			return paymentlist;
		} 
		//전체 결제 목록
		public List<Book_Payment> allpaymentlist(int cpage , int pagesize){
			Connection conn = ConnectionHelper.getConnection("oracle");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			List<Book_Payment> allpaymentlist = new ArrayList<Book_Payment>();
			
			try {
				String sql = "select * from"
						+ "    (select rownum rn,payment_no, isbn, count,to_char(payment_date) as payment_date,sumprice"
						+ "    from"
						+ "        ( SELECT * FROM book_payment ORDER BY payment_no asc )"
						+ "    where rownum <= ?) where rn >= ?";
				pstmt = conn.prepareStatement(sql);
				
				int start = cpage * pagesize - (pagesize -1); //1 * 5 - (5 - 1) >> 1
				int end = cpage * pagesize; // 1 * 5 >> 5
				
				pstmt.setInt(1, end);
				pstmt.setInt(2, start);
				rs = pstmt.executeQuery();
				
				
				while(rs.next()) {
					Book_Payment bookpayment = new Book_Payment();
					bookpayment.setPayment_no(rs.getString("payment_no"));
					bookpayment.setIsbn(rs.getString("isbn"));
					bookpayment.setCount(rs.getInt("count"));
					bookpayment.setPayment_date(rs.getString("payment_date"));
					bookpayment.setSumprice(rs.getInt("sumprice"));
					
					allpaymentlist.add(bookpayment);
					
				}
				
			} catch (Exception e) {
				e.getStackTrace();
			}finally {
				try {
					ConnectionHelper.close(rs);
					ConnectionHelper.close(pstmt);
					ConnectionHelper.close(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return allpaymentlist;
		}
		
		//비동기 
		
		
		//결제 추가
//		public int insertPayment(List<Book_Payment> list, String id) {
		public int insertPayment(List<Book> list, String id, String addr, String detail_addr) {
			Connection conn = ConnectionHelper.getConnection("oracle");
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			int index = 1;
			int row = 0;

			try {
				String sql ="insert all "
						+ "into payment(payment_no, id, payment_addr, payment_detailaddr) " //결제 테이블에 번호, 아이디, 우편번호,  주소 추가
						+ "values(payment_no_seq.nextval, ?, ?, ?) ";
				for(Book book : list) { //결제된 책 목록만큼
					sql += "into book_payment(payment_no, isbn, count, sumprice) "
							+ "values(payment_no_seq.currval, ?, ?, ?) ";
				}
					sql += "select * from dual";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(index++, id);
				pstmt.setString(index++, addr);
				pstmt.setString(index++, detail_addr);
				
				for(Book book : list) { //결제된 모든 책
					pstmt.setString(index++, book.getIsbn());
					pstmt.setInt(index++, 1);
					pstmt.setInt(index++, book.getPrice());
				}
				System.out.println(sql);
				row = pstmt.executeUpdate();
				
				if(row>0) {
					ConnectionHelper.close(pstmt);
					//sql = "delete from cart where id=? and isbn in (select isbn from book_payment where payment_no=(select max(paymnet_no) from book_paymnet where id=?))";
					sql = "delete from cart where id = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, id);
					//pstmt.setString(2, id);
					
					pstmt.executeUpdate();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					ConnectionHelper.close(pstmt);
					ConnectionHelper.close(conn);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return row;
		}
		
}