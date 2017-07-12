package com.etrade.edgepanel.edgedisplay;

import com.etrade.edgepanel.R;
        import com.etrade.edgepanel.data.Stock;
        import com.etrade.edgepanel.data.WatchListManager;
        import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
        import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

import android.app.PendingIntent;
import android.content.ComponentName;
        import android.content.Context;
import android.content.Intent;
import android.util.Log;
        import android.widget.RemoteViews;
import android.widget.Toast;

public class EdgeProvider extends SlookCocktailProvider {
	private static final String DELETE = "com.etrade.edgepanel.action.DELETE_STOCK";
    private static final String ADD = "com.etrade.edgepanel.action.ADD_STOCK";
    private static final WatchListManager watchListManager = new WatchListManager();
    private static final int MAIN_LAYOUT = R.layout.main_view;


    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailManager, int[] cocktailIds) {
        updateEdge(context);
    }

    /**
     * Updates the edge screen with views to display and functionality for buttons
     *
     * @param context
     */
    private void updateEdge(Context context) {
        SlookCocktailManager mgr = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = mgr.getCocktailIds(new ComponentName(context, EdgeProvider.class));
        RemoteViews rv = new RemoteViews(context.getPackageName(), MAIN_LAYOUT);

        Stock s = new Stock("AAPL", "Apple", 151.99, 2.56, 1.07);
        Stock s1 = new Stock("FB", "Facebook", 151.99, -2.56, -1.07);
        Stock s2 = new Stock("MSFT", "Microsoft", 151.99, 2.56, 1.07);
        Stock s3 = new Stock("NFLX", "Netflix", 151.99, 0.00, 0.00);

        Stock[] stocks = {s, s1, s2, s3};

        // Add stocks to Remote View
        for (int i = 0; i < stocks.length; i++) {
            Log.d("Stock update", "New stock added: " + stocks[i].getTicker());
            //Create new remote view using the specified layout file
            RemoteViews listEntryLayout = new RemoteViews(context.getPackageName(), R.layout.list_entry);
            String change = "";
            String percentage = "%(";

            // Set background color to green, red, or gray

            int color = 0;
            if(stocks[i].getPercent_change() > 0.00) {
                color = android.R.color.holo_green_light;
                change += "+";
            } else if(stocks[i].getPercent_change() < 0.00) {
                color = android.R.color.holo_red_light;
            } else {
                color = android.R.color.darker_gray;
            }

            listEntryLayout.setInt(R.id.stock_ticker, "setBackgroundResource", color);
            listEntryLayout.setInt(R.id.stock_name, "setBackgroundResource", color);
            listEntryLayout.setInt(R.id.stock_price, "setBackgroundResource", color);
            listEntryLayout.setInt(R.id.stock_change, "setBackgroundResource", color);
            listEntryLayout.setInt(R.id.stock_perc, "setBackgroundResource", color);

            // Set TextView to appropriate stock text
            listEntryLayout.setTextViewText(R.id.stock_ticker, stocks[i].getTicker());
            listEntryLayout.setTextViewText(R.id.stock_name, stocks[i].getName());
            listEntryLayout.setTextViewText(R.id.stock_price, Double.toString(stocks[i].getValue()));
            change += String.format("%.2f", stocks[i].getDollar_change());
            listEntryLayout.setTextViewText(R.id.stock_change, change);
            percentage += String.format("%.2f", stocks[i].getPercent_change());
            percentage += ")";
            listEntryLayout.setTextViewText(R.id.stock_perc, percentage);

            //Add the new remote view to the parent/containing Layout object
            rv.addView(R.id.main_layout, listEntryLayout);

        }

	    // Set button functionalities
//        rv.setOnClickPendingIntent(R.id.del_button, getPendingSelfIntent(context, DELETE));
//        rv.setOnClickPendingIntent(R.id.add_button, getPendingSelfIntent(context, ADD));

        // Standard updating
        if (cocktailIds != null) {
            for (int id : cocktailIds) {
                mgr.updateCocktail(id, rv);
            }
        }
    }
    
    /**
     * Gets a {@code PendingIntent} object that is designed to target this class (self)
     *
     * @param context
     * @param action
     * @return
     */
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, EdgeProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Filter broadcasts for a specific button's functionality
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("onReceive: ", intent.getAction());

        switch(intent.getAction()) {
            case DELETE:
                Toast.makeText(context, "DELETE", Toast.LENGTH_SHORT).show();
//                watchListManager.deleteStock();
                updateEdge(context);
                break;
            case ADD:
                Toast.makeText(context, "ADD", Toast.LENGTH_SHORT).show();
                //watchListManager.addStock()
                updateEdge(context);
                break;
            default:
                break;
        }
    }
}