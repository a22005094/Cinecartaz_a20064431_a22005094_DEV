package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentWatchedMovieDetailsBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemPhotoBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage

class ViewPagerPhotoAdapter(
    private val photos: List<CustomImage>
    ) : RecyclerView.Adapter<ViewPagerPhotoAdapter.ViewPagerPhotoHolder>() {

    inner class ViewPagerPhotoHolder(val binding: ItemPhotoBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerPhotoHolder {
        return ViewPagerPhotoHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerPhotoHolder, position: Int) {
        Glide.with(holder.binding.ivPhoto.context).
        load(photos[position].imageData) //.asbitmap? check if needed
            .into(holder.binding.ivPhoto)
    }

    override fun getItemCount() = photos.size
}
