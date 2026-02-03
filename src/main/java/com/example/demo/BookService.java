package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

//Service quan ly cac chuc nang lien quan den sach
@Service
public class BookService {
    // Danh sach sach (luu tam trong bo nho)
    private List<Book> books = new ArrayList<>();
    private long nextId = 1;
    
    //Constructor khoi tao du lieu mau
    public BookService() {
        // Them 2 quyen sach mau de test
        books.add(new Book(1,"Spring Boot","Huy Cuong"));
        books.add(new Book(2,"Spring Boot V2","Anh"));
        nextId=3;
    }
    
    // Lay tat ca cac quyen sach
    public List<Book> getAllBooks() {
        return books;
    }
    
    //Them sach moi
    public void addBook(Book book) 
    {
        book.setId((int)nextId++);
        books.add(book);
    }
    
    // Tim sach theo ID
    public Book getBookById(Long id) {
        return books.stream().filter(book -> book.getId()==id.intValue()).findFirst().orElse(null);
    }
    
    //Cap nhat thong tin sach
    public void updateBook(Book updatedBook) {
        for(int i=0;i<books.size();i++) {
            if(books.get(i).getId()==updatedBook.getId()){
                books.set(i,updatedBook);
                break;
            }
        }
    }
    
    // Xoa sach theo ID
    public void deleteBook(Long id) {
        books.removeIf(book -> book.getId()==id.intValue());
    }
}
