CREATE TABLE user_logs
(
    id         SERIAL PRIMARY KEY,
    user_id    INT  NOT NULL,
    action     TEXT NOT NULL,
    changed_at TIMESTAMP DEFAULT now()
);


CREATE OR REPLACE FUNCTION log_user_changes()
    RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO user_logs (user_id, action) VALUES (NEW.user_id, 'INSERT');
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO user_logs (user_id, action) VALUES (NEW.user_id, 'UPDATE');
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO user_logs (user_id, action) VALUES (OLD.user_id, 'DELETE');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_user_logs
    AFTER INSERT OR UPDATE OR DELETE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION log_user_changes();
