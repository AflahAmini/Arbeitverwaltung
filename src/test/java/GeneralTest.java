import arbyte.models.Session;

public class GeneralTest {
    public static void main(String[] args) {
        Session session = new Session();

        for(int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (i == 2 || i == 3)
                session.toggleSessionPause();

            System.out.println(session.getDuration().getSeconds());
        }
    }
}
