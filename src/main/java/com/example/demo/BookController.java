package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//Controller xu ly cac yeu cau lien quan den quan ly sach
@Controller
@RequestMapping("/books")
public class BookController {
    
    //Su dung service de thao tac voi du lieu
    @Autowired
    private BookService bookService;
    
    // Hien thi danh sach tat ca cac sach
    @GetMapping
    public String listBooks(Model model) 
    {
        model.addAttribute("books",bookService.getAllBooks());
        return "books"; // Tra ve trang books.html
    }
    
    //Hien thi form them sach moi
    @GetMapping("/add")
    public String showAddForm(Model model) {
        Book book=new Book();
        model.addAttribute("book", book);
        return "add-book";
    }
    
    // Xu ly them sach moi
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) 
    {
        bookService.addBook(book);
        return "redirect:/books"; //Quay lai trang danh sach
    }
    
    //Hien thi form sua sach
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "edit-book";
    }
    
    // Xu ly cap nhat thong tin sach
    @PostMapping("/edit")
    public String updateBook(@ModelAttribute Book book) {
        bookService.updateBook(book);
        return "redirect:/books";
    }
    
    //Xoa sach
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) 
    {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
