    package model;

    import java.util.Objects;

    public record UserData(String username, String password, String email) {
        @Override
        public int hashCode() {
            return Objects.hash(username, password, email);
        }
    }
