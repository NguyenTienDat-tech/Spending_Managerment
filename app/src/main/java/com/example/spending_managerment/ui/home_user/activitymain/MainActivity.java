package com.example.spending_managerment.ui.home_user.activitymain;

import android.os.Bundle;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.spending_managerment.R;
import com.example.spending_managerment.databinding.ActivityMainBinding;
import com.example.spending_managerment.databinding.PopupMoreBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private PopupWindow moreMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(android.graphics.Color.TRANSPARENT), SystemBarStyle.dark(android.graphics.Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        setupController();
        setupBottomNav();
    }

    private void setupController() {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHomeFragment);
        if (host == null) throw new IllegalStateException("Missing NavHostFragment");
        navController = host.getNavController();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            binding.toolBar.setTitle(destination.getLabel());
            if (moreMenu != null) moreMenu.dismiss();
        });
    }


    private void setupBottomNav() {
        binding.bottomNav.setItemIconSize(getResources().getDimensionPixelSize(R.dimen.bottom_nav_icon_size));
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.more) {
                showMoreMenu();
                // Opening the menu keeps the current screen selected, as in the reference.
                return false;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
        binding.bottomNav.setOnItemReselectedListener(item -> {
            if (moreMenu != null) moreMenu.dismiss();
        });
    }

    private void showMoreMenu() {
        if (moreMenu != null && moreMenu.isShowing()) {
            moreMenu.dismiss();
            return;
        }

        PopupMoreBinding menuBinding = PopupMoreBinding.inflate(getLayoutInflater());
        int width = Math.round(196 * getResources().getDisplayMetrics().density);
        moreMenu = new PopupWindow(menuBinding.getRoot(), width,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        moreMenu.setBackgroundDrawable(
                androidx.appcompat.content.res.AppCompatResources.getDrawable(this, R.drawable.more_menu_background));
        moreMenu.setElevation(8 * getResources().getDisplayMetrics().density);
        moreMenu.setOutsideTouchable(true);

        // UI preview only: these actions close the menu without touching files or data.
        View.OnClickListener closeMenu = view -> moreMenu.dismiss();
        menuBinding.exportExcel.setOnClickListener(closeMenu);
        menuBinding.importExcel.setOnClickListener(closeMenu);
        menuBinding.clearData.setOnClickListener(closeMenu);

        moreMenu.showAtLocation(binding.getRoot(), Gravity.BOTTOM | Gravity.END,
                binding.getRoot().getPaddingRight(),
                binding.bottomNav.getHeight() + binding.getRoot().getPaddingBottom());
    }

    @Override
    protected void onDestroy() {
        if (moreMenu != null) moreMenu.dismiss();
        super.onDestroy();
    }
}
