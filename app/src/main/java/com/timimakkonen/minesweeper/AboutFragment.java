package com.timimakkonen.minesweeper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


/**
 * <p>
 * This fragment class is the fragment that is responsible for displaying the 'about-page'.
 * </p>
 */
public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.about_versionTextView))
                .setText(String.format(getString(R.string.version_name), BuildConfig.VERSION_NAME));

        // setting source code button to open source code webpage
        view.findViewById(R.id.about_sourceCodeButton).setOnClickListener(
                v -> {
                    Uri githubPage = Uri.parse(getString(R.string.source_code_webpage));
                    Intent webpageIntent = new Intent(Intent.ACTION_VIEW, githubPage);
                    List<ResolveInfo> activities =
                            requireActivity()
                                    .getPackageManager()
                                    .queryIntentActivities(webpageIntent,
                                                           PackageManager.MATCH_DEFAULT_ONLY);
                    if (activities.size() > 0) {
                        startActivity(webpageIntent);
                    }
                });

        // setting up feedback button to send email
        view.findViewById(R.id.about_sendFeedbackButton).setOnClickListener(
                v -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.email)});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, appInformationString());
                    if (emailIntent.resolveActivity(requireActivity().getPackageManager()) !=
                        null) {
                        startActivity(emailIntent);
                    }
                });
    }

    private String appInformationString() {

        String output = String.format(Locale.ENGLISH,
                                      getString(R.string.feedback_email_subject),
                                      BuildConfig.VERSION_NAME, Build.VERSION.RELEASE,
                                      Build.VERSION.SDK_INT, Build.DEVICE, Build.MODEL,
                                      Build.PRODUCT);
        Log.d(TAG, String.format("appInformationString: %s", output));
        return output;
    }
}