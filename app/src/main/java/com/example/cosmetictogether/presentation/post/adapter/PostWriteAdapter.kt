package com.example.cosmetictogether.presentation.post.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.databinding.ItemPostWritePhotoBinding

class PostWriteAdapter(
    private val selectedImages: MutableList<String>,
    private val onImageDeleted: (String) -> Unit // Activity에서 처리할 콜백 함수
) : RecyclerView.Adapter<PostWriteAdapter.PostWriteViewHolder>() {

    inner class PostWriteViewHolder(private val binding: ItemPostWritePhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUri: String) {
            // 이미지 URI를 ImageView에 설정
            binding.selectedImageView.setImageURI(Uri.parse(imageUri))

            // 이미지 삭제 버튼 클릭 시 처리
            binding.deleteImageButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // RecyclerView의 목록에서 이미지 삭제
                    val removedImage = selectedImages[position]
                    selectedImages.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, selectedImages.size)

                    // ViewModel과 연동하여 Activity에 변경 사항 알림
                    onImageDeleted(removedImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostWriteViewHolder {
        val binding = ItemPostWritePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostWriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostWriteViewHolder, position: Int) {
        holder.bind(selectedImages[position])
    }

    override fun getItemCount(): Int = selectedImages.size

    // 이미지 목록 업데이트 시 사용
    fun submitList(images: List<String>) {
        selectedImages.clear()
        selectedImages.addAll(images)
        notifyDataSetChanged()
    }
}
