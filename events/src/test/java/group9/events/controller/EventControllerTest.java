package group9.events.controller;

import group9.events.domain.dto.EventCommentsDto;
import group9.events.domain.entity.*;
import group9.events.repository.*;
import group9.events.security.sec_dto.TokenResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("dev")
class EventControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private EventUsersRepository eventUsersRepository;

    private TestRestTemplate template;
    private HttpHeaders headers;
    private Event testEvent;
    private String adminAccessToken;
    private String userAccessToken;

    private final String TEST_EVENT_TITLE_FOR_ADMIN = "EventAdmin";
    private final  String TEST_EVENT_TITLE_FOR_USER ="EventUser";
    private final String TEST_EVENT_CHANGING="Modified Event";
    private String TEST_COMMENTS = "This is a test comment";

    private String ROLE_PARTICIPANT ="ROLE_PARTICIPANT";

    private final String TEST_ADMIN_NAME = "Test Admin";
    private final String TEST_USER_NAME = "Test User";
    private final String LAST_NAME_FOR_ALLE = "Test";
    private final String TEST_PASSWORD = "TestPasword!";
    private final String ROLE_ADMIN_TITLE = "ROLE_ADMIN";
    private final String ROLE_USER_TITLE = "ROLE_USER";
    private final String EMAIL_FOR_USER = "usertest@test.com";
    private final String EMAIL_FOR_ADMIN = "admintest@test.com";
    private final String GENDER_MALE="Male";
    private final String GENDER_FEMALE = "Female";
    private final Date DATE_OF_BIRTH;
    {
        try {
            DATE_OF_BIRTH = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final String PHONE ="+123456789";
    private final String PFOTO = "example.com";


    private final String BEARER_PREFIX= "Bearer ";
    private final String URL_PREFIX = "http://localhost:";
    private final String AUTH_RESOURCE_NAME="/api/auth";
    private final String EVENT_RESOURCE_NAME = "/api/events";
    private final String LOGIN_ENDPOINT = "/login";

    private final String ACTIVE_ENDPOINT="/active";
    private final String ARCHIVE_ENDPOINT="/archive";
    private final String SLASH ="/";
    private final String APPLAY_ENDPOINT = "/apply";
    private final String CANCEL_ENDPOINT ="/cancel";
    private final String MY_ENDPOINT= "/my-points";
    private final String MY_EVENT_ENDPOINT ="/my-event";
    private final String COMMENTS_ENDPOINT = "/comments";


    private final String SET_ADDRESS_START ="Berlin";
    private final String SET_ADRESS_END ="München";
    private final LocalDateTime DATE_START_FOR_EVENT_ADMIN = LocalDateTime.now().plusDays(2);
    private final LocalDateTime DATE_END_FOR_EVENT_ADMIN = LocalDateTime.now().plusDays(3);
    private final LocalDateTime DATE_START_FOR_EVENT_USER = LocalDateTime.now().minusDays(3);
    private final LocalDateTime DATE_END_FOR_EVENT_USER = LocalDateTime.now().minusDays(2);

    private final BigDecimal COST = new BigDecimal(200);

    private final int MAXIMAL_NUMBERS_FOR_EVENT = 5;

    static Long ID_EVENTS_CREATED_USER;
    static Long ID_EVENTS_CREATED_ADMIN;
    static Long USER_ID;


    @BeforeEach
    public void setUp(){

        template = new TestRestTemplate();
        headers= new HttpHeaders();

        testEvent = new Event();

        BCryptPasswordEncoder encoder = null;
        Role roleAdmin;
        Role roleUser = null;

        User admin = userRepository.findByEmail(EMAIL_FOR_ADMIN).orElse(null);
        User user = userRepository.findByEmail(EMAIL_FOR_USER).orElse(null);


        if (admin == null) {
            encoder = new BCryptPasswordEncoder();
            roleAdmin = roleRepository.findByTitle(ROLE_ADMIN_TITLE).orElseThrow(
                    () -> new RuntimeException("Role Admin is missing in the database")
            );
            roleUser = roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(
                    () -> new RuntimeException("Role User is missing in the database")
            );
            admin = new User();
            admin.setLastName(TEST_ADMIN_NAME);
            admin.setFirstName(LAST_NAME_FOR_ALLE);
            admin.setDateOfBirth(DATE_OF_BIRTH);
            admin.setEmail(EMAIL_FOR_ADMIN);
            admin.setPassword(encoder.encode(TEST_PASSWORD));
            admin.setPhone(PHONE);
            admin.setPhone(PFOTO);
            admin.setRoles(Set.of(roleAdmin, roleUser));
            admin.setGender(genderRepository.findByGender(GENDER_MALE));
            admin.setActive(true);
            userRepository.save(admin);
        }

        if (user == null) {
            encoder = encoder == null ? new BCryptPasswordEncoder() : encoder;
            roleUser = roleUser == null ? roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(
                    () -> new RuntimeException("Role User is missing in the database")
            ) : roleUser;
            user = new User();
            user.setLastName(TEST_USER_NAME);
            user.setFirstName(LAST_NAME_FOR_ALLE);
            user.setDateOfBirth(DATE_OF_BIRTH);
            user.setEmail(EMAIL_FOR_USER);
            user.setPassword(encoder.encode(TEST_PASSWORD));
            user.setPhone(PHONE);
            user.setPhoto(PFOTO);
            user.setRoles(Set.of(roleUser));
            user.setGender(genderRepository.findByGender(GENDER_FEMALE));
            user.setActive(true);
            userRepository.save(user);
        }

        USER_ID=userRepository.findByEmail(EMAIL_FOR_USER).get().getId();

        admin.setPassword(TEST_PASSWORD);
        admin.setRoles(null);
        user.setPassword(TEST_PASSWORD);
        user.setRoles(null);

        String url = URL_PREFIX + port + AUTH_RESOURCE_NAME + LOGIN_ENDPOINT;

        HttpEntity<User> request = new HttpEntity<>(admin, headers);
        ResponseEntity<TokenResponseDto> response = template.exchange(url, HttpMethod.POST, request, TokenResponseDto.class);

        assertTrue(response.hasBody(), "Auth response body is empty");

        adminAccessToken = BEARER_PREFIX + response.getBody().getAccessToken();

        request = new HttpEntity<>(user, headers);
        response = template.exchange(url, HttpMethod.POST, request, TokenResponseDto.class);

        assertTrue(response.hasBody(), "Auth response body is empty");
        userAccessToken = BEARER_PREFIX + response.getBody().getAccessToken();
    }




    @Test
    @Order(1)
    public void positiveEventCreationByAdminWithAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME;
        testEvent.setTitle(TEST_EVENT_TITLE_FOR_ADMIN);
        testEvent.setAddressStart(SET_ADDRESS_START);
        testEvent.setAddressEnd(SET_ADRESS_END);
        testEvent.setStartDateTime(DATE_START_FOR_EVENT_ADMIN);
        testEvent.setEndDateTime(DATE_END_FOR_EVENT_ADMIN);
        testEvent.setCost(COST);
        testEvent.setMaximalNumberOfParticipants(MAXIMAL_NUMBERS_FOR_EVENT);
        headers.set("Authorization", adminAccessToken);
        HttpEntity<Event> request = new HttpEntity<>(testEvent, headers);
        ResponseEntity<Event> response = template.exchange(url,HttpMethod.POST,request,Event.class);
        ID_EVENTS_CREATED_ADMIN = response.getBody().getId();
        assertEquals(testEvent.getTitle(),response.getBody().getTitle());
    }



    @Test
    @Order(2)
    public void negativeEventCreationWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME;
        testEvent.setTitle(TEST_EVENT_TITLE_FOR_ADMIN);
        testEvent.setAddressStart(SET_ADDRESS_START);
        testEvent.setAddressEnd(SET_ADRESS_END);
        testEvent.setStartDateTime(DATE_START_FOR_EVENT_ADMIN);
        testEvent.setEndDateTime(DATE_END_FOR_EVENT_ADMIN);
        testEvent.setCost(COST);
        testEvent.setMaximalNumberOfParticipants(MAXIMAL_NUMBERS_FOR_EVENT);
        HttpEntity<Event> request = new HttpEntity<>(testEvent, headers);
        ResponseEntity<Event> response = template.exchange(url,HttpMethod.POST,request,Event.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Response has unexpected status");
    }

    @Test
    @Order(3)
    public void positiveEventCreationByUserWithAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME;
        testEvent.setTitle(TEST_EVENT_TITLE_FOR_USER);
        testEvent.setAddressStart(SET_ADDRESS_START);
        testEvent.setAddressEnd(SET_ADRESS_END);
        testEvent.setStartDateTime(DATE_START_FOR_EVENT_USER);
        testEvent.setEndDateTime(DATE_END_FOR_EVENT_USER);
        testEvent.setCost(COST);
        testEvent.setMaximalNumberOfParticipants(MAXIMAL_NUMBERS_FOR_EVENT);
        headers.set("Authorization", userAccessToken);
        HttpEntity<Event> request = new HttpEntity<>(testEvent, headers);
        ResponseEntity<Event> response = template.exchange(url,HttpMethod.POST,request,Event.class);
        ID_EVENTS_CREATED_USER = response.getBody().getId();
        assertEquals(testEvent.getTitle(), response.getBody().getTitle());
    }



    @Test
    @Order(4)
    public void positiveGettingAllActiveEventsWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+ACTIVE_ENDPOINT;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event[]> response = template.exchange(url, HttpMethod.GET, request, Event[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");
        assertTrue(response.hasBody(), "Response doesn't have a body");
        Event[] events = response.getBody();
        assertNotNull(events, "Events should not be null");
    }


    @Test
    @Order(5)
    public void positiveGettingAllArchiveEventsWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+ARCHIVE_ENDPOINT;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event[]> response = template.exchange(url, HttpMethod.GET, request, Event[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");
        assertTrue(response.hasBody(), "Response doesn't have a body");
        Event[] events = response.getBody();
        assertNotNull(events, "Events should not be null");
    }



    @Test
    @Order(6)
    public void positiveGettingEventByIdWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event> response=template.exchange(url,HttpMethod.GET,request, Event.class);
        assertEquals(HttpStatus.OK,response.getStatusCode(),"Response has unexpected status");
        assertTrue(response.hasBody(),"Response doesn't have a body");
    }

    @Test
    @Order(7)
    public void negativeGettingEventWhichDoesNotExistByIdWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+10000;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event> response=template.exchange(url,HttpMethod.GET,request, Event.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode(),"Response has unexpected status");
    }

    @Test
    @Order(8)
    public void positiveEventChangingByUserWithAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER;
        Event modifededEvent = new Event();
        modifededEvent.setTitle(TEST_EVENT_CHANGING);
        modifededEvent.setAddressStart(SET_ADDRESS_START);
        modifededEvent.setAddressEnd(SET_ADRESS_END);
        modifededEvent.setStartDateTime(DATE_START_FOR_EVENT_USER);
        modifededEvent.setEndDateTime(DATE_END_FOR_EVENT_USER);
        modifededEvent.setCost(COST);
        modifededEvent.setMaximalNumberOfParticipants(MAXIMAL_NUMBERS_FOR_EVENT);
        headers.set("Authorization", userAccessToken);
        HttpEntity<Event> request = new HttpEntity<>(modifededEvent, headers);
        ResponseEntity<Event> response = template.exchange(url,HttpMethod.PUT,request,Event.class);
        ID_EVENTS_CREATED_USER = response.getBody().getId();
        assertEquals(modifededEvent.getTitle(),response.getBody().getTitle());
    }

    @Test
    @Order(9)
    public void negativeEventChangingByUserWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER;
        Event modifededEvent = new Event();
        modifededEvent.setTitle(TEST_EVENT_CHANGING);
        modifededEvent.setAddressStart(SET_ADDRESS_START);
        modifededEvent.setAddressEnd(SET_ADRESS_END);
        modifededEvent.setStartDateTime(DATE_START_FOR_EVENT_USER);
        modifededEvent.setEndDateTime(DATE_END_FOR_EVENT_USER);
        modifededEvent.setCost(COST);
        modifededEvent.setMaximalNumberOfParticipants(MAXIMAL_NUMBERS_FOR_EVENT);
        HttpEntity<Event> request = new HttpEntity<>(modifededEvent, headers);
        ResponseEntity<Event> response = template.exchange(url,HttpMethod.PUT,request,Event.class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    @Test
    @Order(10)
    public void negativeNotExcistEventChangingByUserWithAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+10000;
        Event modifededEvent = new Event();
        modifededEvent.setTitle(TEST_EVENT_CHANGING);
        modifededEvent.setAddressStart(SET_ADDRESS_START);
        modifededEvent.setAddressEnd(SET_ADRESS_END);
        modifededEvent.setStartDateTime(DATE_START_FOR_EVENT_USER);
        modifededEvent.setEndDateTime(DATE_END_FOR_EVENT_USER);
        modifededEvent.setCost(COST);
        modifededEvent.setMaximalNumberOfParticipants(MAXIMAL_NUMBERS_FOR_EVENT);
         headers.set("Authorization", adminAccessToken);
        HttpEntity<Event> request = new HttpEntity<>(modifededEvent, headers);
        ResponseEntity<Event> response = template.exchange(url,HttpMethod.PUT,request,Event.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }


    @Test
    @Order(11)
    public void positiveTestApplyInEvent(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN+APPLAY_ENDPOINT;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = template.exchange(url,HttpMethod.POST,request,Void.class);
        assertEquals(HttpStatus.OK,response.getStatusCode(),"Response has unexpected status");
        List<EventUsers> lisOfEvents = eventUsersRepository.findEventUsersByEvent_Id(ID_EVENTS_CREATED_ADMIN).orElse(Collections.EMPTY_LIST);
        EventUsers eventUsers= lisOfEvents.stream().filter(x->x.getRoleForEvent().getTitle().equals(ROLE_PARTICIPANT)).findFirst().orElse(null);
        assertTrue(eventUsers!=null);
    }

    @Test
    @Order(12)
    public void negativeTestApplyInOwnEvent(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN+APPLAY_ENDPOINT;
        headers.set("Authorization", adminAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = template.exchange(url,HttpMethod.POST,request,Void.class);
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode(),"Response has unexpected status");
    }

    @Test
    @Order(13)
    public void negativeTestApplyInEventWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN+APPLAY_ENDPOINT;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = template.exchange(url,HttpMethod.POST,request,Void.class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode(),"Response has unexpected status");
    }

    @Test
    @Order(14)
    public void positiveGetMyPointsInEvent(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+MY_ENDPOINT;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event[]> response = template.exchange(url,HttpMethod.GET,request,Event[].class);
        Optional<Event> eventOptional = Arrays.stream(response.getBody())
                .filter(x -> x.getId().equals(ID_EVENTS_CREATED_ADMIN))
                .findFirst();
        assertTrue(eventOptional.isPresent());
    }

    @Test
    @Order(15)
    public void negativeGetMyPointsInEventWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+MY_ENDPOINT;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event[]> response = template.exchange(url,HttpMethod.GET,request,Event[].class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode(),"Response has unexpected status");
    }





    @Test
    @Order(16)
    public void positiveTestCancelInEvent(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN+CANCEL_ENDPOINT;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = template.exchange(url,HttpMethod.DELETE,request,Void.class);
        assertEquals(HttpStatus.OK,response.getStatusCode(),"Response has unexpected status");
        List<EventUsers> lisOfEvents = eventUsersRepository.findEventUsersByEvent_Id(ID_EVENTS_CREATED_USER).orElse(Collections.EMPTY_LIST);
        EventUsers eventUsers= lisOfEvents.stream().filter(x->x.getRoleForEvent().getTitle().equals(ROLE_PARTICIPANT)).findFirst().orElse(null);
        assertTrue(eventUsers==null);
    }

    @Test
    @Order(17)
    public void negativeTestCancelInEventWhereNotRegistered(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER+CANCEL_ENDPOINT;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = template.exchange(url,HttpMethod.DELETE,request,Void.class);
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode(),"Response has unexpected status");
    }

    @Test
    @Order(18)
    public void positiveWriteComments(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER+COMMENTS_ENDPOINT;
        headers.set("Authorization", adminAccessToken);
        EventComments eventComments = new EventComments();
        eventComments.setComments(TEST_COMMENTS);
        HttpEntity<EventComments> request = new HttpEntity<>(eventComments,headers);
        ResponseEntity<EventCommentsDto> response =template.exchange(url,HttpMethod.POST,request,EventCommentsDto.class);
        assertEquals(response.getBody().getComments(),eventComments.getComments());
    }

    @Test
    @Order(19)
    public void negativeWriteCommentsWithoutAuthorization(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER+COMMENTS_ENDPOINT;
        EventComments eventComments = new EventComments();
        eventComments.setComments(TEST_COMMENTS);
        HttpEntity<EventComments> request = new HttpEntity<>(eventComments,headers);
        ResponseEntity<EventCommentsDto> response =template.exchange(url,HttpMethod.POST,request,EventCommentsDto.class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }


    @Test
    @Order(20)
    public void negativeWriteCommentsInEventWhichNotEnded(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN+COMMENTS_ENDPOINT;
        headers.set("Authorization", adminAccessToken);
        EventComments eventComments = new EventComments();
        eventComments.setComments(TEST_COMMENTS);
        HttpEntity<EventComments> request = new HttpEntity<>(eventComments,headers);
        ResponseEntity<EventCommentsDto> response =template.exchange(url,HttpMethod.POST,request,EventCommentsDto.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }


    @Test
    @Order(21)
    public void seeCommentsWithoutRegistration(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER+COMMENTS_ENDPOINT;
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<EventCommentsDto[]> response =template.exchange(url,HttpMethod.GET,request,EventCommentsDto[].class);
        EventCommentsDto  eventCommentsDto= Arrays.stream(response.getBody()).filter(x->x.getComments().equals(TEST_COMMENTS)).findFirst().orElse(null);
        assertTrue(eventCommentsDto!=null);
    }

    @Test
    @Order(22)
    public void positiveSeeMyCreatedEvent(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+MY_EVENT_ENDPOINT;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event[]> response = template.exchange(url,HttpMethod.GET,request,Event[].class);
        Optional<Event> eventOptional = Arrays.stream(response.getBody())
                .filter(x -> x.getId().equals(ID_EVENTS_CREATED_USER))
                .findFirst();
        assertTrue(eventOptional.isPresent());
    }

    @Test
    @Order(23)
    public void positiveRemoveEventByUser(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_USER;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event> response=template.exchange(url,HttpMethod.DELETE,request, Event.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");
        assertEquals(response.getBody().getId(),ID_EVENTS_CREATED_USER);
    }


    @Test
    @Order(24)
    public void negativeRemoveEventByUserNotOwner(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN;
        headers.set("Authorization", userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event> response=template.exchange(url,HttpMethod.DELETE,request, Event.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Response has unexpected status");
    }

    @Test
    @Order(25)
    public void positiveRemoveEventByAdmin(){
        String url = URL_PREFIX+port+EVENT_RESOURCE_NAME+SLASH+ID_EVENTS_CREATED_ADMIN;
        headers.set("Authorization", adminAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Event> response=template.exchange(url,HttpMethod.DELETE,request, Event.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");
        assertEquals(response.getBody().getId(),ID_EVENTS_CREATED_ADMIN);
    }

}