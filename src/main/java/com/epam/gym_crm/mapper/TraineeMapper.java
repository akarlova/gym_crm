package com.epam.gym_crm.mapper;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.dto.request.TraineeRegistrationRequest;
import com.epam.gym_crm.dto.response.RegistrationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct Mapper Interface for Trainee Entity Conversions
 *
 * MapStruct is a code generator that creates type-safe, performant mapping code
 * at COMPILE TIME. It generates an implementation class of this interface
 * with all the mapping logic.
 *
 * Why Use MapStruct?
 * ==================
 *
 * WITHOUT MapStruct (Manual Mapping):
 * -----------------------------------
 * Trainee trainee = new Trainee();
 * User user = new User();
 * user.setFirstName(request.getFirstName());
 * user.setLastName(request.getLastName());
 * user.setActive(true);
 * trainee.setUser(user);
 * trainee.setDateOfBirth(request.getDateOfBirth());
 * trainee.setAddress(request.getAddress());
 * // 10+ lines of boilerplate code!
 *
 * WITH MapStruct:
 * ---------------
 * Trainee trainee = traineeMapper.toEntity(request);
 * // Just 1 line! MapStruct generates all the code above automatically
 *
 * Benefits:
 * - Type-safe: Compile-time checking (no runtime reflection)
 * - Fast: No reflection overhead (direct method calls)
 * - Less code: No manual mapping boilerplate
 * - Maintainable: Changes to entities automatically update mappings
 * - Testable: Generated implementation can be tested
 *
 * How It Works:
 * =============
 * 1. You define this interface with @Mapper annotation
 * 2. During Maven compile phase, MapStruct annotation processor runs
 * 3. MapStruct generates TraineeMapperImpl.java in target/generated-sources
 * 4. Generated class implements this interface with actual mapping code
 * 5. Spring creates a bean of TraineeMapperImpl (because componentModel = "spring")
 * 6. You inject this mapper into controllers/services and use it
 *
 * Generated Implementation Location:
 * target/generated-sources/annotations/com/epam/gym_crm/mapper/TraineeMapperImpl.java
 *
 * @author Gym CRM Team
 * @version 1.0.0
 * @since 2025-10-25
 *
 * @see org.mapstruct.Mapper
 * @see com.epam.gym_crm.domain.Trainee
 * @see com.epam.gym_crm.dto.request.TraineeRegistrationRequest
 */
@Mapper(componentModel = "spring")
/**
 * @Mapper Annotation Explanation:
 * ================================
 *
 * Tells MapStruct: "This is a mapper interface, generate implementation for it"
 *
 * componentModel = "spring":
 * --------------------------
 * Tells MapStruct to generate a Spring-compatible implementation.
 *
 * What it does:
 * - Adds @Component annotation to generated implementation class
 * - Makes the mapper a Spring bean
 * - Allows dependency injection (can inject into controllers)
 * - Spring manages the lifecycle (singleton by default)
 *
 * Generated class will look like:
 * @Component
 * public class TraineeMapperImpl implements TraineeMapper {
 *     // generated mapping methods
 * }
 *
 * Usage in Controller:
 * private final TraineeMapper traineeMapper;  // Spring auto-injects this
 *
 * Other componentModel Options:
 * - "default": Plain Java class, no framework integration
 * - "cdi": For CDI/Jakarta EE applications
 * - "jsr330": For JSR-330 (@Named) applications
 *
 * Why Spring?
 * - We're using Spring Framework
 * - Enables dependency injection
 * - Consistent with other Spring beans
 * - Easy testing with @MockBean
 */
public interface TraineeMapper {

    /**
     * Converts TraineeRegistrationRequest DTO to Trainee Entity
     *
     * Purpose:
     * ========
     * Transforms data from the API layer (DTO) to the domain layer (Entity).
     * This separation follows clean architecture principles:
     * - DTOs are for external communication (JSON ↔ Java)
     * - Entities are for business logic and database persistence
     *
     * Mapping Flow:
     * =============
     * TraineeRegistrationRequest (DTO)     →     Trainee (Entity)
     * {                                          {
     *   firstName: "John"                          id: null (ignored)
     *   lastName: "Doe"                            user: {
     *   dateOfBirth: "1990-05-15"                    firstName: "John"
     *   address: "123 Main St"                       lastName: "Doe"
     * }                                              isActive: true
     *                                                username: null (set later)
     *                                                password: null (set later)
     *                                              }
     *                                              dateOfBirth: 1990-05-15
     *                                              address: "123 Main St"
     *                                              trainings: [] (empty, ignored)
     *                                              trainers: [] (empty, ignored)
     *                                            }
     *
     * @param request - TraineeRegistrationRequest containing user input from API
     * @return Trainee entity ready for business logic processing
     *
     * @see TraineeRegistrationRequest
     * @see Trainee
     */
    @Mapping(target = "id", ignore = true)
    /**
     * @Mapping for 'id' field:
     * ========================
     *
     * target = "id":
     *   - Refers to the 'id' field in Trainee entity
     *   - This is the destination field we're configuring
     *
     * ignore = true:
     *   - Tells MapStruct: "Don't try to map this field at all"
     *   - Leave it as null (for new entities)
     *   - ID will be auto-generated by database (IDENTITY strategy)
     *
     * Why ignore?
     * -----------
     * 1. This is for NEW trainees (registration)
     * 2. ID doesn't exist yet (will be assigned by database)
     * 3. Request DTO doesn't have an ID field
     * 4. If we don't ignore it, MapStruct might try to find a source field
     *
     * Database Flow:
     * - Trainee created with id = null
     * - Repository saves trainee
     * - Database generates ID (1, 2, 3, ...)
     * - Hibernate updates trainee object with generated ID
     *
     * Alternative (if updating existing trainee):
     * @Mapping(source = "id", target = "id")  // Would copy ID from DTO
     */

    @Mapping(target = "user", expression = "java(createUser(request))")
    /**
     * @Mapping for 'user' field (Complex Nested Object):
     * ==================================================
     *
     * target = "user":
     *   - The 'user' field in Trainee entity
     *   - Trainee has a @OneToOne relationship with User
     *
     * expression = "java(createUser(request))":
     *   - Uses a custom Java expression for mapping
     *   - Calls the createUser() method defined below
     *   - Allows complex mapping logic that simple field mapping can't handle
     *
     * Why use expression?
     * -------------------
     * 1. User creation is COMPLEX:
     *    - Need to set firstName, lastName from request
     *    - Need to set isActive = true (default for new users)
     *    - Username and password are NOT in request (generated later by service)
     *
     * 2. Can't do simple field mapping:
     *    - Request doesn't have a 'user' field
     *    - Request has firstName/lastName directly
     *    - Need to CREATE a new User object
     *
     * 3. Provides flexibility:
     *    - Can add business logic
     *    - Can set default values
     *    - Can handle complex object construction
     *
     * Flow:
     * 1. MapStruct sees this expression
     * 2. Calls createUser(request) method
     * 3. Method returns a User object
     * 4. User object is assigned to trainee.user
     *
     * Alternative (if request had a nested user object):
     * @Mapping(source = "user", target = "user")  // Simple field copy
     *
     * Generated Code (simplified):
     * trainee.setUser(createUser(request));
     */

    @Mapping(target = "trainings", ignore = true)
    /**
     * @Mapping for 'trainings' collection:
     * ====================================
     *
     * target = "trainings":
     *   - The trainings Set in Trainee entity
     *   - @OneToMany relationship (one trainee, many training sessions)
     *
     * ignore = true:
     *   - Don't map this field
     *   - Leave as empty collection or null
     *
     * Why ignore?
     * -----------
     * 1. This is REGISTRATION - trainee just created
     * 2. New trainees have NO training sessions yet
     * 3. Request DTO doesn't include trainings
     * 4. Trainings are added later through separate endpoints
     *
     * Lifecycle:
     * 1. Trainee registers → trainings = empty
     * 2. Admin/Trainer assigns training sessions → trainings populated
     * 3. Future endpoints will handle adding trainings
     *
     * If we didn't ignore:
     * - MapStruct would look for 'trainings' in request
     * - Not found → might cause error or unwanted behavior
     */

    @Mapping(target = "trainers", ignore = true)
    /**
     * @Mapping for 'trainers' collection:
     * ===================================
     *
     * target = "trainers":
     *   - The trainers Set in Trainee entity
     *   - @ManyToMany relationship (trainee can have multiple trainers)
     *
     * ignore = true:
     *   - Don't map this field
     *   - Leave as empty collection
     *
     * Why ignore?
     * -----------
     * 1. New trainees start with NO assigned trainers
     * 2. Request doesn't specify trainers (user can't choose during registration)
     * 3. Trainers are assigned later by admin or through separate endpoints
     * 4. Similar to trainings - relationship built after creation
     *
     * Business Rule:
     * - Trainee registers first
     * - Later: "Update Trainee's Trainer List" endpoint assigns trainers
     * - Keeps registration simple and focused
     */

    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    /**
     * @Mapping for 'dateOfBirth' field (Simple Field Mapping):
     * ========================================================
     *
     * source = "dateOfBirth":
     *   - The 'dateOfBirth' field in TraineeRegistrationRequest (source)
     *
     * target = "dateOfBirth":
     *   - The 'dateOfBirth' field in Trainee entity (destination)
     *
     * What it does:
     * -------------
     * Copies the value directly from request to entity.
     *
     * trainee.setDateOfBirth(request.getDateOfBirth());
     *
     * Note on Redundancy:
     * -------------------
     * This mapping is actually OPTIONAL!
     *
     * MapStruct Automatic Mapping:
     * - If source and target have fields with SAME NAME
     * - And SAME TYPE (or compatible types)
     * - MapStruct automatically maps them
     *
     * So this could be removed:
     * ❌ @Mapping(source = "dateOfBirth", target = "dateOfBirth")
     *
     * MapStruct will still map it automatically because:
     * - Request has: LocalDate dateOfBirth
     * - Entity has: LocalDate dateOfBirth
     * - Same name + same type = automatic mapping
     *
     * When you NEED explicit mapping:
     * 1. Different field names:
     *    @Mapping(source = "dob", target = "dateOfBirth")
     *
     * 2. Type conversion:
     *    @Mapping(source = "birthDate", target = "dateOfBirth", dateFormat = "yyyy-MM-dd")
     *
     * 3. Nested fields:
     *    @Mapping(source = "personalInfo.birthDate", target = "dateOfBirth")
     *
     * 4. Documentation:
     *    Sometimes kept for clarity, even if automatic
     *
     * Best Practice:
     * - Omit obvious mappings (same name + type)
     * - Keep only complex or non-obvious mappings
     * - Makes code cleaner and easier to read
     */

    @Mapping(source = "address", target = "address")
    /**
     * @Mapping for 'address' field:
     * =============================
     *
     * source = "address":
     *   - The 'address' field in TraineeRegistrationRequest
     *
     * target = "address":
     *   - The 'address' field in Trainee entity
     *
     * Simple copy: trainee.setAddress(request.getAddress());
     *
     * Note: Same as dateOfBirth mapping above
     * -------
     * This is also OPTIONAL due to automatic mapping.
     *
     * Both have:
     * - Same field name: "address"
     * - Same type: String
     * - MapStruct would map automatically
     *
     * Kept here for:
     * - Explicit documentation
     * - Consistency with other mappings
     * - Makes intent clear to readers
     *
     * Could be safely removed without changing behavior.
     */
    Trainee toEntity(TraineeRegistrationRequest request);

    /**
     * Custom method for creating User object from request
     *
     * Why a Default Method?
     * =====================
     *
     * Java 8 Feature:
     * - Interfaces can have default methods with implementations
     * - Before Java 8: interfaces only had abstract methods
     * - Now: can provide default implementations
     *
     * MapStruct Usage:
     * - MapStruct can call default methods in mapping expressions
     * - Allows complex logic without needing a separate helper class
     * - Keeps related mapping logic together in one interface
     *
     * Purpose Here:
     * =============
     * Creates a User object for the Trainee entity because:
     *
     * 1. Complex Construction:
     *    - Request doesn't have a User object
     *    - Need to create User from individual fields
     *    - Need to set default values (isActive = true)
     *
     * 2. Business Logic:
     *    - New users are ALWAYS active by default
     *    - Username/password are NOT set here (generated by service later)
     *    - Separates "what comes from user" vs "what system generates"
     *
     * 3. Reusability:
     *    - Can be called from multiple mapping methods
     *    - Centralized User creation logic
     *    - Easy to modify defaults in one place
     *
     * Flow in Context:
     * ================
     *
     * 1. Controller receives request:
     *    {
     *      "firstName": "John",
     *      "lastName": "Doe",
     *      "dateOfBirth": "1990-05-15",
     *      "address": "123 Main St"
     *    }
     *
     * 2. Controller calls:
     *    Trainee trainee = traineeMapper.toEntity(request);
     *
     * 3. MapStruct generated code calls:
     *    User user = createUser(request);
     *
     * 4. This method executes:
     *    - Creates new User()
     *    - Sets firstName = "John"
     *    - Sets lastName = "Doe"
     *    - Sets isActive = true
     *    - Returns user object
     *
     * 5. MapStruct generated code:
     *    trainee.setUser(user);
     *
     * 6. Service layer later adds:
     *    user.setUsername("John.Doe");
     *    user.setPassword(generatePassword());
     *
     * Why Username/Password Not Set Here?
     * ====================================
     *
     * Separation of Concerns:
     * - Mapper: Transform data structure (DTO → Entity)
     * - Service: Apply business logic (generate username/password)
     *
     * Username Generation Logic:
     * - Might need to check database for duplicates
     * - Might need to add numeric suffix (John.Doe1, John.Doe2)
     * - Too complex for mapper (mappers should be simple)
     *
     * Password Generation:
     * - Requires random generation
     * - Might need encryption/hashing
     * - Security concern (service layer responsibility)
     *
     * Mapper Responsibility:
     * - Only handle STRUCTURAL transformation
     * - Copy values from DTO to entity
     * - Set default values that don't need logic
     * - DON'T access database, DON'T call services
     *
     * Alternative Approach (NOT RECOMMENDED):
     * ----------------------------------------
     * We COULD generate username here:
     *
     * default User createUser(TraineeRegistrationRequest request) {
     *     User user = new User();
     *     user.setFirstName(request.getFirstName());
     *     user.setLastName(request.getLastName());
     *     user.setUsername(request.getFirstName() + "." + request.getLastName());
     *     user.setPassword(UUID.randomUUID().toString()); // Bad!
     *     user.setActive(true);
     *     return user;
     * }
     *
     * Problems:
     * ❌ Can't check for duplicate usernames (needs database access)
     * ❌ Password generation not secure
     * ❌ Violates single responsibility (mapper doing business logic)
     * ❌ Harder to test
     * ❌ Harder to change logic later
     *
     * Current Approach (RECOMMENDED):
     * --------------------------------
     * ✅ Mapper: Simple data transformation
     * ✅ Service: Complex business logic
     * ✅ Clear separation of concerns
     * ✅ Easy to test each layer
     * ✅ Easy to modify business rules
     *
     * @param request - Source DTO containing user input
     * @return User entity with basic information populated
     */
    default User createUser(TraineeRegistrationRequest request) {
        // Create new User entity
        User user = new User();

        // Copy basic information from request
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Set default: new users are active
        user.setActive(true);

        // Note: username and password are NOT set here
        // They will be generated by the service layer using business logic

        return user;
    }

    /**
     * Converts Trainee Entity to RegistrationResponse DTO
     *
     * Purpose:
     * ========
     * Transforms data from the domain layer (Entity) back to the API layer (DTO)
     * for sending as HTTP response.
     *
     * Use Case:
     * =========
     * After trainee is successfully created and persisted:
     * 1. Service generates username and password
     * 2. Service saves trainee to database
     * 3. Controller needs to return credentials to user
     * 4. This mapper extracts username/password and creates response DTO
     *
     * Mapping Flow:
     * =============
     * Trainee (Entity)                    →    RegistrationResponse (DTO)
     * {                                        {
     *   id: 1                                    username: "John.Doe"
     *   user: {                                  password: "aB3$xY9zQ"
     *     username: "John.Doe"               }
     *     password: "aB3$xY9zQ"
     *     firstName: "John"
     *     lastName: "Doe"
     *     isActive: true
     *   }
     *   dateOfBirth: 1990-05-15
     *   address: "123 Main St"
     * }
     *
     * Notice:
     * - Only username and password are extracted
     * - Other fields (firstName, address, etc.) are NOT in response
     * - Response is minimal (only what user needs to know)
     *
     * Why Only Username/Password?
     * ===========================
     *
     * Security & UX:
     * - User needs credentials to login
     * - Password is only shown ONCE (during registration)
     * - After this, password is never sent again (security)
     * - User should save these credentials
     *
     * Response Example:
     * {
     *   "username": "John.Doe",
     *   "password": "aB3$xY9zQ"
     * }
     *
     * User should see:
     * "Account created successfully!
     *  Username: John.Doe
     *  Password: aB3$xY9zQ
     *  Please save these credentials - password will not be shown again!"
     *
     * @param trainee - Trainee entity after successful creation (with generated username/password)
     * @return RegistrationResponse DTO containing credentials for the user
     *
     * @see RegistrationResponse
     * @see Trainee
     */
    @Mapping(source = "user.username", target = "username")
    /**
     * @Mapping for 'username' (Nested Field Access):
     * ==============================================
     *
     * source = "user.username":
     *   - Navigate from Trainee to nested User object
     *   - Then access the username field
     *   - Dot notation for nested property access
     *
     * target = "username":
     *   - The username field in RegistrationResponse DTO
     *
     * Flow:
     * 1. Start with Trainee entity
     * 2. Get trainee.getUser() → returns User object
     * 3. Get user.getUsername() → returns String username
     * 4. Set response.setUsername(username)
     *
     * Generated Code (simplified):
     * response.setUsername(trainee.getUser().getUsername());
     *
     * Null Safety:
     * ------------
     * MapStruct automatically generates null checks:
     *
     * if (trainee != null) {
     *     User user = trainee.getUser();
     *     if (user != null) {
     *         response.setUsername(user.getUsername());
     *     }
     * }
     *
     * Why Nested Mapping?
     * -------------------
     * - Trainee doesn't have username directly
     * - Username is in the associated User entity
     * - Need to traverse object graph
     * - MapStruct makes this easy with dot notation
     *
     * Alternative (without MapStruct):
     * --------------------------------
     * String username = null;
     * if (trainee != null && trainee.getUser() != null) {
     *     username = trainee.getUser().getUsername();
     * }
     * response.setUsername(username);
     *
     * Much more verbose and error-prone!
     */

    @Mapping(source = "user.password", target = "password")
    /**
     * @Mapping for 'password' (Nested Field Access):
     * ==============================================
     *
     * source = "user.password":
     *   - Same nested navigation as username
     *   - Trainee → User → password
     *
     * target = "password":
     *   - The password field in RegistrationResponse
     *
     * Generated: response.setPassword(trainee.getUser().getPassword());
     *
     * CRITICAL SECURITY NOTE:
     * =======================
     *
     * This is the ONLY time we ever send password in response!
     *
     * Registration Flow:
     * 1. User registers → password generated
     * 2. Password sent in response (this mapping)
     * 3. User sees password and should save it
     * 4. Password NEVER sent again in any other endpoint
     *
     * Other Endpoints:
     * - GET /api/trainees/{username} → Does NOT include password
     * - PUT /api/trainees → Does NOT return password
     * - Login → User provides password, we verify, but don't return it
     *
     * Why Show Password Once?
     * -----------------------
     * ✅ User needs it to login
     * ✅ System generated it (user doesn't know it)
     * ✅ User must save it
     *
     * Security Best Practices:
     * ------------------------
     * ✅ Password shown only during registration
     * ✅ Stored hashed in database (not plain text)
     * ✅ Never logged (should be masked in logs)
     * ✅ Transmitted over HTTPS only
     * ✅ Never included in subsequent API responses
     *
     * In Real Production:
     * -------------------
     * Might want to:
     * - Send password via email instead
     * - Force password change on first login
     * - Use temporary password that expires
     * - Implement "forgot password" flow
     *
     * For This Assignment:
     * --------------------
     * Simple approach: generate and return password
     * User responsible for saving it
     *
     * Future Enhancement:
     * -------------------
     * Could add password hashing in mapper:
     *
     * @Mapping(target = "password", expression = "java(hashPassword(trainee.getUser().getPassword()))")
     *
     * default String hashPassword(String plainPassword) {
     *     // Use BCrypt or similar
     *     return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
     * }
     *
     * But better to handle in service layer!
     */
    RegistrationResponse toRegistrationResponse(Trainee trainee);
}