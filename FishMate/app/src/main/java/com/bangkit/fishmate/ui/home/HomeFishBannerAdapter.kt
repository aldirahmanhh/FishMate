import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.R
import com.bangkit.fishmate.ui.home.HomeFragment

class HomeFishBannerAdapter(
    private val context: HomeFragment,
    private val images: List<Int> // List of local image resource IDs
) : RecyclerView.Adapter<HomeFishBannerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.vp_item_banner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fishbanner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageResId = images[position]
        holder.imageView.setImageResource(imageResId)

        holder.imageView.setOnClickListener { view ->
            view.findNavController().navigate(R.id.navigation_setting)
        }
    }

    override fun getItemCount(): Int = images.size
}
