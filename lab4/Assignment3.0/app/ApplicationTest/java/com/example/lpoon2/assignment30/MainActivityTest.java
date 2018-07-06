package com.example.lpoon2.assignment30;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GithubAuthProvider;
import com.robotium.solo.Solo;

import java.util.List;

/**
 * Created by lpoon2 on 10/30/2017.
 */
@SuppressWarnings("rawtypes")
public class MainActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;
    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.example.lpoon2.assignment30.MainActivity";
    private FirebaseAuth auth;

    private static Class<?> launcherActivityClass;

    static{
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public MainActivityTest() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    /**
     * Set up environment for testing framewokr - Robotium
     * @throws Exception
     */
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    /**
     * Event of closing down the unit test
     * @throws Exception
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    /**
     * The main test suite: it triggers all the fragments by clicking different tabs in the views
     */
    public void testRun() {
        solo.waitForActivity("MainActivity", 2000);
        solo.assertCurrentActivity("wrong activity", MainActivity.class);

        try {
            testFirebase();
            testDrawer();
            testProile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing the texts appear in the drawer navigation
     * - i.e. Profile, Following  Follower, etc...
     * @throws Exception
     */
    @TargetApi(11)
    public void testDrawer() throws Exception {

        Fragment fragment = getActivity().getFragmentManager().findFragmentById(R.id.nav);

        //check if drawer options exist
        assertTrue(solo.waitForText("Profile")); // Assertion
        assertTrue(solo.waitForText("Following")); // Assertion
        assertTrue(solo.waitForText("Follower")); // Assertion
        assertTrue(solo.waitForText("Repositories")); // Assertion

        //test profile information for lpoon2
        assertTrue(solo.waitForText("Larry Poon")); // Assertion
        assertTrue(solo.waitForText("lpoon2")); // Assertion
        assertTrue(solo.waitForText("created")); // Assertion

        // go to test forr repo
        solo.clickOnText("repositories");
        solo.waitForFragmentById(R.id.profile, 5000);
        testRepo();

    }

    /**
     * Unit test for our follower fragment
     * we test further with the web view generate by the follower fragment
     * the target follower is Zach
     * @throws Exception
     */
    @TargetApi(11)
    public void testFollower() throws Exception {

        // go to test for following
        solo.clickOnText("following");
        Fragment fragment = getActivity().getFragmentManager().findFragmentById(R.id.nav);

        solo.waitForFragmentById(R.id.followers, 5000);

        View v = solo.getView(R.id.followers);

        //check layout exist
        assertTrue(v != null);
        assertTrue(solo.waitForText("Zach")); // Assertion

        solo.clickOnText("Zach");

        assertTrue(solo.waitForText("2")); // Assertion
        assertTrue(solo.waitForText("Zach")); // Assertion
        assertTrue(solo.waitForText("4")); // Assertion
        assertTrue(solo.waitForText("following")); // Assertion
        assertTrue(solo.waitForText("created")); // Assertion

        solo.clickOnText("repositories");

        List<View> vs = solo.getCurrentViews();
        CheckBox cb = (CheckBox) solo.getView(R.id.checkBox);
        assertTrue(cb.isChecked() == false);

    }

    @TargetApi(11)
    public void testRepo() throws Exception {

        solo.clickOnText("Amdocs");

        //checking text in the web view
        assertTrue(solo.waitForText("master")); // Assertion
        assertTrue(solo.waitForText("Pulse")); // Assertion
        assertTrue(solo.waitForText("Issues")); // Assertion
        assertTrue(solo.waitForText("Pull requests")); // Assertion
        assertTrue(solo.waitForText("Jump")); // Assertion


        List<View> vs = solo.getCurrentViews();
        CheckBox cb = (CheckBox) solo.getView(R.id.checkBox);

        assertTrue(cb.isChecked() == false);
        assertTrue(vs.size() > 3 );

    }

    /**
     * until test for testing Following page, which displays following users' information
     * @throws Exception
     */
    @TargetApi(11)
    public void testFollowing() throws Exception {

        // go to test for following
        solo.clickOnText("following");
        Fragment fragment = getActivity().getFragmentManager().findFragmentById(R.id.nav);

        solo.waitForFragmentById(R.id.following, 5000);
        View v = solo.getView(R.id.followers);

        //check layout exist
        assertTrue(v != null);
        assertTrue(solo.waitForText("Zach")); // Assertion
        assertTrue(solo.waitForText("Google")); // Assertion
        assertTrue(solo.waitForText("Search")); // Assertion
        assertTrue(solo.waitForText("vish")); // Assertion

    }

    /**
     * Unit test for notification. Due of lack of followers, my account doesn't have a lot of
     * notification, hopefully one is sufficent
     * @throws Exception
     */
    @TargetApi(11)
    public void testNotification() throws Exception {

        // go to test for following
        solo.clickOnActionBarItem(R.id.action_example);
        Fragment fragment = getActivity().getFragmentManager().findFragmentById(R.id.nav);

        solo.waitForFragmentById(R.id.notification_list, 5000);
        View v = solo.getView(R.id.followers);

        //check layout exist
        assertTrue(v != null);
        assertTrue(solo.waitForText("Invitation")); // Assertion
        assertTrue(solo.waitForText("vpahwa")); // Assertion
        assertTrue(solo.waitForText("GoodVsi")); // Assertion
        assertTrue(solo.waitForText("join")); // Assertion

        //testing message read/unread
        CheckBox cb = (CheckBox) solo.getView(R.id.checkBox2);
        assertTrue(cb.isChecked() == false);

    }

    /**
     * Unit test for the search functionality
     * we will simulating entering query and click on the saerch button
     * @throws Exception
     */
    @TargetApi(11)
    public void testSearch() throws Exception{

        solo.clickOnText("following");
        Button btn = (Button) solo.getView(R.id.search_buttoon);
        EditText text = (EditText) solo.getView(R.id.search);

        text.setText("lpoon2");
        solo.clickOnButton("Search");

        assertTrue(solo.waitForText("lpoon2")); // Assertion
        List<View> vs = solo.getCurrentViews();
        assertTrue(vs.size() == 2);

    }


    /**
     * Unit test for the search functionality
     * we will simulating entering query and click on the saerch button
     * @throws Exception
     */
    @TargetApi(11)
    public void testStatistics() throws Exception{
        solo.clickOnText("Stats");

    }

    /**
     * Unit test for testing Profile fragment, which displays user informaion
     * @throws Exception
     */
    @TargetApi(11)
    public void testProile() throws Exception {

//        Fragment fragment = getActivity().getFragmentManager().findFragmentById(R.id.profile);

        assertTrue(solo.waitForText("Larry Poon")); // Assertion
        assertTrue(solo.waitForText("lpoon2")); // Assertion
        assertTrue(solo.waitForText("12")); // Assertion
        assertTrue(solo.waitForText("2")); // Assertion
        assertTrue(solo.waitForText("1")); // Assertion

        solo.clickOnText("12");
        solo.waitForFragmentById(R.id.repo, 5000);
        testRepo();
        solo.clickOnText("2");
        solo.waitForFragmentById(R.id.following, 5000);
        testFollowing();
        solo.clickOnText("1");
        solo.waitForFragmentById(R.id.followers, 5000);
        testFollower();
    }

    /**
     * Unit test for testing firebase integration with our app
     * - log in & out functionalities
     * @throws Exception
     */
    @TargetApi(11)
    public void testFirebase() throws Exception {

        String token = "f1ebbe990f6f59ddd489df0206aa487be157e48c";
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        auth = FirebaseAuth.getInstance();

        // test log in
        auth.signInWithEmailAndPassword("lpoon2@illinois.edu", "Cps50240784")
                .addOnCompleteListener(solo.getCurrentActivity(), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        assertTrue(task.isSuccessful());
                    }
                });

        // test log out
        auth.getInstance().signOut();
        assertTrue(auth.getCurrentUser() == null);

    }

}