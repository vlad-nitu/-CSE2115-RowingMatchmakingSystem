package nl.tudelft.cse.sem.template.user.publishers;

import nl.tudelft.cse.sem.template.user.utils.BaseActivity;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
public class ActivityPublisher {
    private final transient UserUtils userUtils;

    public ActivityPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public ActivityPublisher() {
        this.userUtils = new UserUtils("http://localhost:8084/");
    }

    // We still need to find a way here to make a distinction between a competition and a training
    // I would maybe suggest to send all the information necessary for a competition together with the type
    // and then to make the logic division between this in activity?

    /**
     * Create a new activity.
     *
     * @param activity all the information that needs to included in the activity
     */
    public BaseActivity createActivity(BaseActivity activity) throws Exception {
        try {
            Response res = userUtils.postRequest("/createActivity", activity);
            BaseActivity baseActivity = res.readEntity(new GenericType<BaseActivity>(){});
            return baseActivity;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
