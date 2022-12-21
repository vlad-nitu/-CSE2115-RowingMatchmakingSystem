package com.example.activitymicroservice.services;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.repositories.ActivityRepository;
import com.example.activitymicroservice.utils.Pair;
import com.example.activitymicroservice.utils.TimeSlot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ActivityService {

    private final transient ActivityRepository activityRepository;

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    /**
     * Gets a list of TimeSlots and returns all the Activities that are included in those timeslots.
     *
     * @param timeSlots List of TimeSlots
     * @return List of Activities
     */
    public List<Activity> getActivitiesByTimeSlot(List<TimeSlot> timeSlots) {
        List<Activity> activityList = new ArrayList<>();
        for (Activity activity : activityRepository.findAll()) {
            for (TimeSlot timeSlot : timeSlots) {
                if (activity.getTimeSlot().isIncluded(timeSlot)) {
                    activityList.add(activity);
                    break;
                }
            }
        }
        return activityList;
    }

    public Activity findActivity(Long activityId) {
        return activityRepository.findById(activityId).get();
    }

    public Optional<Activity> findActivityOptional(Long activityId) {
        return activityRepository.findById(activityId);
    }

    @Transactional
    public void deleteById(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    /**
     * Returns a list of timeslots, given a list of activity ids.
     *
     * @param activityIds a list of activity ids
     * @return a list of timeslots
     */
    public List<TimeSlot> getTimeSlotsByActivityIds(List<Long> activityIds) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        for (Long activityId : activityIds) {
            timeSlots.add(findActivity(activityId).getTimeSlot());
        }
        return timeSlots;
    }

    /**
     * Occupies a certain position in an Activity.
     *
     * @param posTaken Pair of a Long and String object representing the ID of the activity and the
     *                 position occupied respectively
     * @throws Exception if the position does not exist in the Activity.
     */
    public void takeSpot(Pair<Long, String> posTaken) throws Exception {
        Optional<Activity> activity = this.findActivityOptional(posTaken.getFirst());
        if (activity.isEmpty()) {
            throw new Exception("Activity was not found");
        }
        //Probably will be needed for a test because it says that the list is immutable, so I create a new one
        List<String> updatedPositions = new ArrayList<>(activity.get().getPositions());
        if (updatedPositions.remove(posTaken.getSecond())) {
            activity.get().setPositions(updatedPositions);
            activityRepository.save(activity.get());
        } else {
            throw new Exception("The wished position was not found");
        }
    }

    /**
     * Occupies a certain position in an Activity.
     *
     * @param posTaken Pair of a Long and String object representing the ID of the activity and the
     *                 position occupied respectively
     * @throws Exception if the position does not exist in the Activity.
     */
    public List<String> unenrollPosition(Pair<Long, String> posTaken) throws Exception {
        Optional<Activity> activity = this.findActivityOptional(posTaken.getFirst());
        if (activity.isEmpty()) {
            throw new Exception("Activity was not found");
        }
        //need to do this for a test because it says that the list is immutable, so I create a new one
        List<String> updatedPositions = new ArrayList<>(activity.get().getPositions());
        updatedPositions.add(posTaken.getSecond());
        activity.get().setPositions(updatedPositions);
        activityRepository.save(activity.get());
        return activity.get().getPositions();
    }

    /**
     * Checks if an Activity can be approached by a User with a certain set of features.
     *
     * @param activity the given Activity
     * @param gender the gender of the User
     * @param certificate the certificate of the User
     * @param organisation the organisation of the User
     * @param competitiveness the competitiveness of the User
     * @param listPositions the list of positions the User can occupy
     * @param position the position the User wants to occupy
     * @return a boolean representing weather the User is eligible for the Activity or not
     */
    public boolean checkUser(Activity activity, Character gender,
                             String certificate, String organisation,
                             boolean competitiveness, List<String> listPositions, String position) {
        if (activity instanceof Competition) {
            if (((Competition) activity).isCompetitive() != competitiveness) {
                return false;
            }
            if (((Competition) activity).getGender() != gender) {
                return false;
            }
            if (!Objects.equals(((Competition) activity).getOrganisation(), organisation)) {
                return false;
            }
        }
        if (!Objects.equals(activity.getCertificate(), certificate)) {
            return false;
        }
        return listPositions.contains(position);
    }
}
