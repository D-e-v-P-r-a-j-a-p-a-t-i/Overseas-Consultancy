package com.Overseas.overseasproject.controller;

import com.Overseas.overseasproject.model.Appointment;
import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.model.UserNotFoundException;
import com.Overseas.overseasproject.repository.AppointmentRepository;
import com.Overseas.overseasproject.repository.UserRepository;
import com.Overseas.overseasproject.service.ErrorLogService;
import com.Overseas.overseasproject.service.UserService;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@SessionAttributes({"user_name", "myAppointments", "role"})

public class MainController {

    private Boolean userAdded = false;
    private Boolean userEdited = false;
    private Boolean userDeleted = false;
    private Boolean editUserFail = false;
    private Long userID = 0L;
    private String role = "";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ErrorLogService errorLogService;

    /**
     * Displays the change password page for a specific user.
     *
     * @param id    the user ID.
     * @param model the model to pass attributes to the view.
     * @return the name of the change password page view.
     */
    @GetMapping("/change-pwd/{id}")
    public String getChangePwd(@PathVariable("id") String id, Model model) {
        try {
            Optional<User> usr = Optional.ofNullable(userService.get((long) Integer.parseInt(id)));
            if (!usr.isPresent()) {
                throw new UsernameNotFoundException("user not found");
            } else {
                model.addAttribute("user", usr.get());
            }
            return "change-pwd";
        } catch (UserNotFoundException e) {
            errorLogService.logError("Error While fetching User for Changing Password", e.getMessage());
            return "error"; // Assuming you have an error page
        }
    }

    /**
     * Processes the change password request for a user.
     *
     * @param id  the user ID.
     * @param req the HttpServletRequest containing the new password.
     * @return redirect to the sign-in page if successful, otherwise an error page.
     */
    @PostMapping("/post-change-pwd/{id}")
    public String postChangePwd(@PathVariable("id") String id, HttpServletRequest req) {
        try {
            String password = req.getParameter("newPassword");
            Optional<User> usr = Optional.ofNullable(userService.get((long) Integer.parseInt(id)));
            if (!usr.isPresent()) {
                throw new UsernameNotFoundException("user not found");
            } else {
                User old = usr.get();
                old.setPassword(new BCryptPasswordEncoder().encode(password));
                userRepository.save(old);
            }
            return "redirect:/signin";
        } catch (UserNotFoundException e) {
            errorLogService.logError("Error while changing password", e.getMessage());
            return "error"; // Assuming you have an error page
        }
    }

    /**
     * Displays the login page and handles authentication errors.
     *
     * @param request the HttpServletRequest containing error messages.
     * @param model   the model to pass attributes to the view.
     * @return the name of the login page view or redirects to the about page if already authenticated.
     */
    @GetMapping("/")
    public String getSignin(HttpServletRequest request, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth instanceof AnonymousAuthenticationToken) {
                String errorMessage = request.getParameter("error");
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    model.addAttribute("errorMessage", errorMessage);
                }
                return "login";
            }
            return "redirect:/about";
        } catch (Exception e) {
            errorLogService.logError("Error while attempting to sign in", e.getMessage());
            return "redirect:/signin";
        }
    }

    /**
     * Displays the about page based on the user's role.
     *
     * @param model     the model to pass attributes to the view.
     * @param principal the principal object to get the currentUser user's details.
     * @return the name of the appropriate about page view.
     */
    @GetMapping("/about")
    public String dashboard(Model model, Principal principal) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String email = principal.getName();
            User currentUser = userService.getUserByRoleAndEmail("", email);
            System.out.println(currentUser);
            System.out.println(email);

            if (currentUser == null) {
                throw new UsernameNotFoundException("user not found");
            } else if (currentUser.getRole().equals("student")) {
                System.out.println("started");
                role = "student";
                String user_name = currentUser.getName();

                ArrayList<Appointment> myAppointments = appointmentRepository.findAllByStudentId(currentUser.getId());
                model.addAttribute("user_name", user_name);
                model.addAttribute("myAppointments", myAppointments);
                model.addAttribute("role", role);
                System.out.println(myAppointments);
                System.out.println(currentUser.getId());
                System.out.println("ended");
                return manageUserPagination(model, 1);
            } else if (currentUser.getRole().equals("admin")) {
                System.out.println("started");
                role = "admin";
                model.addAttribute("userAdded", userAdded);
                userAdded = false;
                model.addAttribute("userEdited", userEdited);
                userEdited = false;
                model.addAttribute("userDeleted", userDeleted);
                userDeleted = false;
                model.addAttribute("editUserFail", editUserFail);
                editUserFail = false;
                model.addAttribute("role", role);
                System.out.println("ended");
                return manageUserPagination(model, 1);
            } else {
                role = "consultant";
                String user_name = userService.getUserByRoleAndEmail(role, principal.getName()).getName();
                ArrayList<Appointment> myAppointments = appointmentRepository.findAllByConsultantId(currentUser.getId());

                List<Long> studentIds = myAppointments.stream().map(Appointment::getStudentId).collect(Collectors.toList());

                ArrayList<User> students = (ArrayList<User>) userRepository.findAllById(studentIds);

                model.addAttribute("user", user_name);
                model.addAttribute("students", students);
                model.addAttribute("appointments", myAppointments);
                System.out.println(students);
                return "consultant-about";
            }
        } catch (Exception e) {
            errorLogService.logError("Error while showing dashboard", e.getMessage());
            return "redirect:/signin";
        } catch (UserNotFoundException e) {
            errorLogService.logError("Error while fetching user by Email", e.getMessage());
            return "redirect:/signin";
        }
    }

    /**
     * Manages paginated users for the admin about page.
     *
     * @param model the model to pass attributes to the view.
     * @param page  the page number to display.
     * @return the name of the admin about page view.
     */
    @GetMapping("/page/{page}")
    public String manageUserPagination(Model model, @PathVariable("page") int page) {
        try {
            System.out.println("last start");
            int pageSize = 5;
            if (page < 1) {
                return "redirect:/about"; // Redirect to a default page if an invalid page number is provided
            }

            // Retrieve session attributes
            String role = (String) model.getAttribute("role");
            String user = (String) model.getAttribute("user_name");
            List<Appointment> myAppointments = (List<Appointment>) model.getAttribute("myAppointments");

            System.out.println("Role: " + role);
            System.out.println("User: " + user);
            System.out.println("Appointments: " + myAppointments);

            Page<User> cityPage = userService.getPaginatedUsers(role, PageRequest.of(page - 1, pageSize));
            System.out.println("last end");

            int totalPages = cityPage.getTotalPages();
            System.out.println("Total pages: " + totalPages);

            if (totalPages > 0 && page <= totalPages) { // Check if the page number is within valid bounds
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            } else {
                return "redirect:/about"; // Redirect to a default page if an invalid page number is provided
            }

            model.addAttribute("currentUserPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", cityPage.getTotalElements());
            model.addAttribute("fewusers", cityPage.getContent());

            // Add session attributes back to the model
            model.addAttribute("user", user);
            model.addAttribute("myappointments", myAppointments);

            if (role.equals("admin")) {
                return "admin-about";
            } else {
                return "student-about";
            }
        } catch (Exception e) {
            errorLogService.logError("Error while fetching page " + page, e.getMessage());
            return "redirect:/about";
        }
    }



    /**
     * Logs out the currentUserly authenticated user.
     *
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A redirect to the login page with a logout parameter.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                // Logging out the user
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return "redirect:/login?logout";
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error while logging out", e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Displays the add user page.
     *
     * @param request The HTTP servlet request.
     * @param model   The model to be populated with data.
     * @return The add user page.
     */
    @GetMapping("/add-user")
    public String addUserPage(HttpServletRequest request, Model model) {
        try {
            String error = request.getParameter("error");
            if (error != null && error.equals("emailExists")) {
                model.addAttribute("errorMessage", "Email already exists");
            }
            return "add-user";
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error while adding user", e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Generates and adds records to the database.
     *
     * @return A redirect to the about page.
     */
    @GetMapping("/addRecords")
    public String addRecords() {
        try {
            // Generating fake users and saving to the database
            Faker cursor = new Faker();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 9000; i++) {
                for (int j = 0; j < 100; j++) {
                    users.add(new User(cursor.name().name(), encoder.encode(cursor.internet().password()), cursor.internet().emailAddress(), i % 2 == 0 ? "student" : "consultant"));
                }
                userRepository.saveAll(users);
                System.out.println("1000 records");
                users = new ArrayList<>();
            }
            return "redirect:/about";
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error in addRecords", e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Adds a new user to the database.
     *
     * @param req   The HTTP servlet request containing user details.
     * @param model The model to be populated with data.
     * @return A redirect to the about page.
     */
    @PostMapping("/add-user")
    public String addUserToDb(HttpServletRequest req, Model model) {
        try {
            String name = req.getParameter("name");
            String password = req.getParameter("password");
            String email = req.getParameter("email");
            String role = req.getParameter("role");

            // Checking if email already exists
            User existingUser = userService.getUserByRoleAndEmail(role, email);
            if (existingUser != null) {
//                throw new UserAlreadyExistsException("User with same email is already registered!!");
                return "redirect:/add-user?error=emailExists";
            }

            // Saving new user
            User user = new User(name, new BCryptPasswordEncoder().encode(password), email, role);
            userService.save(user);
            userAdded = true;
            return "redirect:/about";
        } catch (UserNotFoundException e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error occurred while storing user to database", e.getMessage());
            System.out.println(e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Displays the edit user page for a specified user.
     *
     * @param id      The ID of the user to be edited.
     * @param request The HTTP servlet request.
     * @param model   The model to be populated with data.
     * @return The edit user page.
     */
    @GetMapping("/edit-user")
    public String getEditUserById(@RequestParam(value = "id", defaultValue = "-1") String id, HttpServletRequest request, Model model) {
        try {
            if (id != "-1") {
                userID = (long) Integer.parseInt(id);
            }
            String error = request.getParameter("error");
            if (error != null && error.equals("emailExists")) {
                model.addAttribute("errorMessage", "Email already exists");
            }
            Optional<User> usr = Optional.ofNullable(userService.get(userID));
            if (usr.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            } else {
                model.addAttribute("user", usr.get());
            }
            return "edit-user";
        } catch (UserNotFoundException e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error fetching data of user for edit", e.getMessage());
            return "redirect:/about";
        }
    }


    /**
     * Updates a user's details in the database.
     *
     * @param id  The ID of the user to be updated.
     * @param req The HTTP servlet request containing updated user details.
     * @return A redirect to the about page.
     */
    @PostMapping("/update-user/{id}")
    public String postUpdateUserById(@PathVariable("id") String id, HttpServletRequest req) {
        try {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String role = req.getParameter("role");


            System.out.println("in updte strt");

            User existingUser = userService.getUserByRoleAndEmail(role, email);
            if (existingUser != null && existingUser.getId() != Long.parseLong(id)) {
                editUserFail = true;
                return "redirect:/edit-user";
            } else {
                Optional<User> usr = Optional.ofNullable(userService.get((long) Integer.parseInt(id)));
                if (!usr.isPresent()) {
                    throw new UsernameNotFoundException("user not found");
                } else {
                    User old = usr.get();
                    old.setName(name);
                    old.setEmail(email);
                    old.setPassword(new BCryptPasswordEncoder().encode(password));
                    old.setRole(role);
                    userService.update(Long.valueOf(id), old);
                    userEdited = true;
                }
                System.out.println("in updte end");
                return "redirect:/about";
            }
        } catch (UserNotFoundException e) {
            errorLogService.logError("Error while updating user", e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param id    The ID of the user to be deleted.
     * @param model The model to be populated with data.
     * @return A redirect to the about page.
     */
    @GetMapping("/delete-user/{id}")
    public String deleteUserById(@PathVariable("id") String id, Model model) {
        try {
            userRepository.deleteById((long) Integer.parseInt(id));
            userDeleted = true;
            return "redirect:/about";
        } catch (Exception e) {
            errorLogService.logError("Error while deleting user from database", e.getMessage());
            return "redirect:/about";
        }
    }
}
