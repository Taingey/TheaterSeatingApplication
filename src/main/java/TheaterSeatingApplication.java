import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TheaterSeatingApplication {
    private static final int TOTAL_HALLS = 3;
    private static final int TOTAL_ROWS = 4;
    private static final int TOTAL_SEATS_PER_ROW = 5;
    private static final String[] timeHall = {"Morning (10:00AM - 12:30PM)", "Afternoon (03:00PM - 05:30PM)", "Night (07:00PM - 09:30PM)"};
    private static final String[][] hallSeats = new String[TOTAL_HALLS][TOTAL_ROWS * TOTAL_SEATS_PER_ROW];
    private static final String[] hallNames = {"Hall A", "Hall B", "Hall C"};
    private static final LocalDateTime[] bookingTime = {
            LocalDateTime.now().withHour(10).withMinute(0),    // Morning
            LocalDateTime.now().withHour(15).withMinute(0),    // Afternoon
            LocalDateTime.now().withHour(19).withMinute(0)     // Night
    };
    private static final String AVAILABILITY = "AV";
    private static final String BOOKED = "BO";
    private static final String[][] bookingHistory = new String[100][5];
    private static final DateTimeFormatter showtimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        initializeHallSeats();
        Scanner scanner = new Scanner(System.in);
        String choice;
        do {
            displayMenu();
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            System.out.print("> Enter your choice: ");
            choice = scanner.next();
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");

            switch (choice.toUpperCase()) {
                case "A" -> bookHall(scanner);
                case "B" -> checkHallSeats();
                case "C" -> showtimeChecking();
                case "D" -> viewBookingHistory();
                case "E" -> rebootHall(scanner);
                case "F" -> System.out.println("Exiting Hall Booking System. Goodbye!");
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (!choice.equalsIgnoreCase("F"));
    }

    private static void bookHall(Scanner scanner) {
        Scanner input = new Scanner(System.in);
        System.out.println("\n|=O=O=O=O=O=O=O Hall Booking O=O=O=O=O=O=O=|\n");

        System.out.print("=====> Input Hall Value  : ");
        int e = input.nextInt();
        for (int i = 1; i <= e; i++) {
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            System.out.println("> Hall Values [" + i + "]");
            int hallChoice = validateInput(scanner, "> Enter hall choice (1-" + TOTAL_HALLS + "): ", "[1-3]");
            int showtimeChoice = validateInput(scanner, "> Enter showtime choice (1-3): ", "[1-3]");
            int rowChoice = validateInput(scanner, "> Enter row number (1-" + TOTAL_ROWS + "): ", "[1-4]");
            int seatChoice = validateInput(scanner, "> Enter seat number (1-" + TOTAL_SEATS_PER_ROW + "): ", "[1-5]");
            String studentID = String.valueOf(validateInput(scanner, "> Enter student card ID: ", "[A-Za-z0-9]+"));
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");

            if (Objects.equals(hallSeats[hallChoice - 1][calculateSeatIndex(rowChoice, seatChoice)], BOOKED)) {
                System.out.println("Seat already booked. Please choose another seat.");
            } else {
                hallSeats[hallChoice - 1][calculateSeatIndex(rowChoice, seatChoice)] = BOOKED;
                bookingHistory[getBookingIndex()] = new String[]{hallNames[hallChoice - 1],
                        String.valueOf(rowChoice), String.valueOf(seatChoice), studentID,
                        bookingTime[showtimeChoice - 1].format(showtimeFormatter)};

                System.out.println("Booking successful!");
            }
        }
    }

    private static void initializeHallSeats() {
        for (int i = 0; i < TOTAL_HALLS; i++) {
            for (int j = 0; j < TOTAL_ROWS * TOTAL_SEATS_PER_ROW; j++) {
                hallSeats[i][j] = AVAILABILITY;
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n===== Hall Booking System =====\n");
        System.out.println("<A>. Hall Booking");
        System.out.println("<B>. Hall Checking");
        System.out.println("<C>. Showtime Checking");
        System.out.println("<D>. Booking History");
        System.out.println("<E>. Rebooting Hall");
        System.out.println("<F>. Exit");
        System.out.println("\n================================");
    }

    private static void checkHallSeats() {
        char rowLabel = 'A';

        for (int i = 0; i < TOTAL_HALLS; i++) {
            System.out.println("\nHall-" + (i + 1));
            for (int j = 0; j < TOTAL_ROWS; j++) {
                for (int k = 0; k < TOTAL_SEATS_PER_ROW; k++) {
                    int seatIndex = j * TOTAL_SEATS_PER_ROW + k;
                    System.out.printf("|%c-%d::%s| ", rowLabel, k + 1, hallSeats[i][seatIndex]);
                }
                System.out.println();
                rowLabel++;
            }
            rowLabel = 'A';
        }
    }

    private static void showtimeChecking() {
        System.out.println("\n-+-+-+-+-+-+-| Showtime Checking |-+-+-+-+-+-+-\n");

        for (int i = 0; i < timeHall.length; i++) {
            System.out.println((i + 1) + ". " + timeHall[i]);
        }
        System.out.println("\n-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-++-+-+-+-++-+-+-");
    }


    private static void viewBookingHistory() {
        System.out.println("\n-+-+-+-+-+-+-| Booking History |-+-+-+-+-+-+-\n");

        boolean hasHistory = false;

        for (String[] booking : bookingHistory) {
            if (booking[0] != null) {
                hasHistory = true;
                LocalDateTime showtime = LocalDateTime.parse(booking[4], showtimeFormatter);
                LocalDateTime bookingTime = LocalDateTime.now(); // Get current date and time
                String formattedBookingTime = bookingTime.format(showtimeFormatter);

                System.out.println(
                        "Hall: " + booking[0] +
                                ", Seat: " + booking[1] +
                                ", Student ID: " + booking[3] +
                                ", Show Time: " + timeHall[Integer.parseInt(booking[1]) - 1] +
                                ", Booking Time: " + formattedBookingTime);
            }
        }

        if (!hasHistory) {
            System.out.println("\t\t\t  Don't have History");
        }

        System.out.println("\n-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
    }

    private static int calculateSeatIndex(int row, int seat) {
        return (row - 1) * TOTAL_SEATS_PER_ROW + (seat - 1);
    }

    private static void rebootHall(Scanner scanner) {
        System.out.println("\n-+-+-+-+-+-+-+-+-+-| Rebooting Hall |-+-+-+-+-+-+-+-+-+-+-");
        int hallChoice = validateInput(scanner, "Enter hall choice to reboot (1-" + TOTAL_HALLS + "):", "[1-3]");
        if (TOTAL_ROWS != 1) {
            for (int i = 0; i < TOTAL_ROWS * TOTAL_SEATS_PER_ROW; i++) {
                hallSeats[hallChoice - 1][i] = AVAILABILITY;
            }
            clearBookingHistory(hallChoice);

            System.out.println("Hall rebooted successfully!");
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        } else {
            System.out.println("Not Booking");
        }
    }

    private static void clearBookingHistory(int hallChoice) {
        for (int i = 0; i < bookingHistory.length; i++) {
            if (bookingHistory[i][0] != null && hallNames[hallChoice - 1].equals(bookingHistory[i][0])) {
                bookingHistory[i] = new String[5];
            }
        }
    }

    private static int validateInput(Scanner scanner, String message, String regex) {
        int input;
        Pattern pattern = Pattern.compile(regex);

        do {
            System.out.print(message);
            while (!scanner.hasNextInt() && !scanner.hasNext()) {
                System.out.println("Invalid input. Please enter a valid value.");
                scanner.next();
            }

            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
            } else {
                scanner.next(); // Consume non-integer input
                input = 0;
            }

            if (input < 1) {
                System.out.println("Invalid input. Please enter a valid value.");
            }
        } while (input < 1 || !matchesRegex(String.valueOf(input), pattern));

        return input;
    }
    private static int getBookingIndex() {
        // Find the next available index in bookingHistory
        for (int i = 0; i < bookingHistory.length; i++) {
            if (bookingHistory[i][0] == null) {
                return i;
            }
        }
        return -1;
    }
    private static boolean matchesRegex(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
