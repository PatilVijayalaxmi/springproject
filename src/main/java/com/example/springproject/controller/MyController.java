package com.example.springproject.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.springproject.dto.MyUSer;
import com.example.springproject.dto.StudentDetails;
import com.example.springproject.helper.HelperForSendingMail;
import com.example.springproject.repository.MyUserRepository;
import com.example.springproject.repository.StudentRepository;


import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@MultipartConfig 
public class MyController {

   
    @Autowired
    MyUserRepository myUserRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    HelperForSendingMail helperForSendingMail;


    @GetMapping("/")
    public String loadHome() {
        return "home.html";
    }

    // ------------------------------------------
    @GetMapping("/signup")
    public String loadSignUp(ModelMap map) {
        map.put("myUSer", new MyUSer());
        return "signup.html";
    }

    // -----------------------------------------
    @GetMapping("/login")
    public String loadLogin() {
        return "login.html";
    }

    // ------------------------------------------------
    @PostMapping("/signup")
    public String signup(@Valid MyUSer myUSer, BindingResult result, ModelMap map) {
        // return user.toString();

        if (myUserRepository.existsByEmail(myUSer.getEmail())) {
            result.rejectValue("email", "error.email", "emailālready exist");
        }

        if (result.hasErrors())
            // return "There is some error";
            return "signup.html";

        else {
            // return user.toString();

            int otp = new Random().nextInt(10000, 100000);
            myUSer.setOtp(otp);
            System.out.println(myUSer.getOtp());
            helperForSendingMail.sendEmail(myUSer);
            myUserRepository.save(myUSer);

            // logic for sending otp//
            map.put("success", "otp sent success,Check your Email");
            map.put("id", myUSer.getId());

            return "enter_otp.html";
        }

    }

    // ---------------------------------------------------
    @PostMapping("/verify_otp")
    public String verify(@RequestParam int id, @RequestParam int otp, ModelMap map) {
        MyUSer myUSer = myUserRepository.findById(id).orElseThrow();
        if (myUSer.getOtp() == otp) {
            myUSer.setVerified(true);
            myUserRepository.save(myUSer);
            map.put("success", "Account created successfully");
            return "home.html";

        } else {
            map.put("failure", "Invalid OTP ,Try Again");
            map.put("id", myUSer.getId());
            return "enter_otp.html";
        }
    }

    // ----------------------------------------------
    @PostMapping("/login")
    public String Loginpage(@RequestParam String email, @RequestParam String password, ModelMap modelMap,
            HttpSession httpSession) {
        MyUSer myUSer = myUserRepository.findByEmail(email);
        System.out.println(myUSer);
        if (myUSer == null) {
            modelMap.put("failure", "Invalid email address");
            return "login.html";
        } else {
            if (password.equals(myUSer.getPassword())) {
                if (myUSer.isVerified()) {
                    httpSession.setAttribute("user", myUSer);

                    modelMap.put("Success", "Login Success");
                    return "home.html";
                } else {
                    int otp = new Random().nextInt(100000, 10000000);
                    myUSer.setOtp(otp);
                    System.out.println(otp);
                    helperForSendingMail.sendEmail(myUSer);
                    myUserRepository.save(myUSer);
                    modelMap.put("success", "OTP sent success,check your email");
                    modelMap.put("id", myUSer.getId());
                    return "enter_otp.html";
                }
            } else {
                modelMap.put("failure", "Invalid password");
                return "login.html";
            }

        }
    }

    // ----------------------------------------
    @GetMapping("/insert")
    public String Insert(HttpSession httpSession, ModelMap modelMap) {
        if (httpSession.getAttribute("user") != null) {
            return "insert.html";
        } else {
            modelMap.put("failure", "Invalid Session");
            return "login.html";
        }
    }

    // ------------------------------------------------
    @GetMapping("/fetch")
    public String FetchAll(HttpSession httpSession, ModelMap modelMap) {
        if (httpSession.getAttribute("user") != null) {
            List<StudentDetails> list=studentRepository.findAll();
            if(list.isEmpty()){
                modelMap.put("failure","not data found");
                return "home.html";
            }
            else{
                modelMap.put("list",list);
                return "fetch.html";
            }
           
        } else {
            modelMap.put("failure", "Invalid Session");
            return "login.html";
        }
    }

    // ----------------------------------------
    @GetMapping("/update")
    public String update(HttpSession httpSession, ModelMap modelMap,@RequestParam int id) {
        if (httpSession.getAttribute("user") != null) {
            StudentDetails studentDetails=studentRepository.findById(id).orElseThrow();
            modelMap.put("studentdetails",studentDetails);
            return "update.html";
        } else {
            modelMap.put("failure", "Invalid Session");
            return "login.html";
        }
    }
    

    // ---------------------------------------------

    @GetMapping("/delete")
    public String Delete(HttpSession httpSession, ModelMap modelMap,@RequestParam int id) {
        if (httpSession.getAttribute("user") != null) {
        studentRepository.deleteById(id);
        modelMap.put("Success", "Record deleted successfully");
            return FetchAll(httpSession, modelMap);
        } else {
            modelMap.put("failure", "Invalid Session");
            return "login.html";

        }
    }

    // -----------------------------------------
    @GetMapping("/logout")
    public String Logout(HttpSession httpSession, ModelMap map) {
        map.put("Success", "logout Successfully");
        httpSession.removeAttribute("user");
        return "home.html";
    }

//------------------------------------------
@PostMapping("/insert")
public String insert(@ModelAttribute StudentDetails studentDetails,HttpSession httpSession, ModelMap modelMap, @RequestParam MultipartFile img)
{
    if (httpSession.getAttribute("user") != null) {
        studentDetails.setPicture(addToCloudinary(img));
        studentRepository.save(studentDetails);
        modelMap.put("Success","Record Save successfully");
        return "home.html";
    } else {
        modelMap.put("failure", "Invalid Session");
        return "login.html";
    }


}
//------------------------------------------------

public String addToCloudinary(MultipartFile img) {

		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "debitzvkn", "api_key",
				"464939958547331", "api_secret", "Em426xQ41acX5AsVjS7iq0KkJzw", "secure", true));

		Map resume = null;
		try {
			Map<String, Object> uploadOptions = new HashMap<String, Object>();
			uploadOptions.put("folder", "StudentManagement");
			resume = cloudinary.uploader().upload(img.getBytes(), uploadOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (String) resume.get("url");
	}

//-----------------------------------------------
@PostMapping("/update")
public String update(@ModelAttribute StudentDetails studentDetails,HttpSession httpSession, ModelMap modelMap, @RequestParam MultipartFile img,@RequestParam int id)
{
    if (httpSession.getAttribute("user") != null) {
        studentDetails.setPicture(addToCloudinary(img));
        studentRepository.save(studentDetails);
        modelMap.put("Success","Record Save successfully");
        return "fetch.html";
    } else {
        modelMap.put("failure", "Invalid Session");
        return "login.html";
    }
}
}


