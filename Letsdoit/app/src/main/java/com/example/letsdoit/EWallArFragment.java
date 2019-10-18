package com.example.letsdoit;

import com.example.letsdoit.ArActivity;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Objects;

/**
 * ArFragment that configures the Ar Scene
 */
public class EWallArFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);

        session.configure(config);

        this.getArSceneView().setupSession(session);

        ((ArActivity) (Objects.requireNonNull(getActivity()))).setUpDatabase(config, session);

        return config;
    }

}