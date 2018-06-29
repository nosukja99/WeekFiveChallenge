package com.example.weekfivechallenge;


import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MemeRepository memeRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMeme(Model model)
    {
        model.addAttribute("memes", memeRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String memeForm(Model model)
    {
        model.addAttribute("meme", new Meme());
        return "memeform";
    }

    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute Meme meme, BindingResult result,
                              @RequestParam("file") MultipartFile file)
    {
        if(result.hasErrors())
        {
            return "memeform";
        }
        if(file.isEmpty())
        {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            meme.setImage(uploadResult.get("url").toString());
            memeRepository.save(meme);
        }catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("meme", memeRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMeme(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("meme", memeRepository.findById(id));
        return "memeform";
    }

    @RequestMapping("/delete/{id}")
    public String deleteMeme(@PathVariable("id") long id)
    {
        memeRepository.deleteById(id);
        return "redirect:/";
    }
}
